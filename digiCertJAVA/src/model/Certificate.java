package model;

import java.time.LocalDate;

public class Certificate {
    public String id;
    public String holderName;
    public String course;
    public String institution;
    public String grade;
    public LocalDate issueDate;
    public LocalDate expiryDate;
    public String status = "ACTIVE";
    public String signature = "";
    public String revokedReason = "";

    public String signatureInput() {
        return holderName + "|" + course + "|" + institution + "|"
                + grade + "|" + issueDate + "|" + expiryDate;
    }
}
