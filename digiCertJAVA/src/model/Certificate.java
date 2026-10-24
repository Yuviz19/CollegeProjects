package model;

import java.time.LocalDate;

public class Certificate {

    private String id;
    private String holderName;
    private String course;
    private String institution;
    private String grade;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
    private String signature;
    private String revokedReason;

    public Certificate() {
        this("ACTIVE", "", "");
    }

    public Certificate(String holderName, String course, String institution,
                       String grade, LocalDate issueDate, LocalDate expiryDate) {
        this("ACTIVE", "", "");
        this.holderName = holderName;
        this.course = course;
        this.institution = institution;
        this.grade = grade;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    public Certificate(String status, String signature, String revokedReason) {
        this.status = status;
        this.signature = signature;
        this.revokedReason = revokedReason;
    }

    public String getSignatureInput() {
        return holderName + "|" + course + "|" + institution + "|"
                + grade + "|" + issueDate + "|" + expiryDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String v) { this.holderName = v; }

    public String getCourse() { return course; }
    public void setCourse(String v) { this.course = v; }

    public String getInstitution() { return institution; }
    public void setInstitution(String v) { this.institution = v; }

    public String getGrade() { return grade; }
    public void setGrade(String v) { this.grade = v; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate v) { this.issueDate = v; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate v) { this.expiryDate = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }

    public String getSignature() { return signature; }
    public void setSignature(String v) { this.signature = v; }

    public String getRevokedReason() { return revokedReason; }
    public void setRevokedReason(String v) { this.revokedReason = v; }
}
