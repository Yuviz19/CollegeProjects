#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_STUDENTS 10

typedef struct {
  int submitted_count;
  int evaluated_count;
  pthread_mutex_t lock;
  pthread_cond_t cond_submit;
  pthread_cond_t cond_evaluate;
} ExamMonitor;

ExamMonitor monitor;

void init_monitor() {
  monitor.submitted_count = 0;
  monitor.evaluated_count = 0;
  pthread_mutex_init(&monitor.lock, NULL);
  pthread_cond_init(&monitor.cond_submit, NULL);
  pthread_cond_init(&monitor.cond_evaluate, NULL);
}

void *student_process(void *arg) {
  int id = *(int *)arg;

  pthread_mutex_lock(&monitor.lock);
  printf("\n[Student %d] is uploading files...\n", id);
  sleep(1);
  monitor.submitted_count++;
  printf("[Monitor] Script stored. Total pending evaluation: %d\n",
         monitor.submitted_count);

  pthread_cond_signal(&monitor.cond_submit);
  pthread_mutex_unlock(&monitor.lock);

  pthread_mutex_lock(&monitor.lock);
  while (monitor.evaluated_count == 0) {
    printf("[Student %d] is waiting for their grade...\n", id);
    pthread_cond_wait(&monitor.cond_evaluate, &monitor.lock);
  }

  monitor.evaluated_count--;
  printf(
      "[System] SUCCESS: Student %d has received their Result Certificate.\n",
      id);
  pthread_mutex_unlock(&monitor.lock);

  return NULL;
}

void *teacher_evaluator(void *arg) {
  while (1) {
    pthread_mutex_lock(&monitor.lock);

    while (monitor.submitted_count == 0) {
      pthread_cond_wait(&monitor.cond_submit, &monitor.lock);
    }

    printf("\n[Teacher] Starting evaluation of a new script...\n");
    sleep(2); // Grading takes time

    monitor.submitted_count--;
    monitor.evaluated_count++;

    printf("[Teacher] Grading finished. Signaling Result System.\n");

    pthread_cond_signal(&monitor.cond_evaluate);
    pthread_mutex_unlock(&monitor.lock);
  }
  return NULL;
}

int main() {
  pthread_t teacher;
  pthread_t students[MAX_STUDENTS];
  int student_ids[MAX_STUDENTS];
  int student_count = 0;
  int choice;

  init_monitor();

  pthread_create(&teacher, NULL, teacher_evaluator, NULL);
  pthread_detach(teacher);

  printf("=== Welcome to the Interactive Exam Monitor ===\n");
  printf("1. Add a Student (Submit Exam)\n");
  printf("2. Check System Status\n");
  printf("3. Exit\n");

  while (1) {
    printf("\nAdmin Action > ");
    if (scanf("%d", &choice) != 1)
      break;

    if (choice == 1) {
      if (student_count < MAX_STUDENTS) {
        student_ids[student_count] = student_count + 1;
        pthread_create(&students[student_count], NULL, student_process,
                       &student_ids[student_count]);
        student_count++;
      } else {
        printf("Error: Maximum student capacity reached.\n");
      }
    } else if (choice == 2) {
      pthread_mutex_lock(&monitor.lock);
      printf("\n--- System Status ---\n");
      printf("Scripts Pending Evaluation: %d\n", monitor.submitted_count);
      printf("Results Ready for Pickup: %d\n", monitor.evaluated_count);
      pthread_mutex_unlock(&monitor.lock);
    } else if (choice == 3) {
      printf("Shutting down exam system. Goodbye!\n");
      break;
    } else {
      printf("Invalid choice.\n");
    }
  }

  return 0;
}
