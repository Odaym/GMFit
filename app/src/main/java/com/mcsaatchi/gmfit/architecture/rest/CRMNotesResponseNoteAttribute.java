package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CRMNotesResponseNoteAttribute {
  @SerializedName("DocumentBody") @Expose private String documentBody;
  @SerializedName("FileName") @Expose private String fileName;
  @SerializedName("AnnotationId") @Expose private String annotationId;
  @SerializedName("MimeType") @Expose private String mimeType;
  @SerializedName("CreatedBy") @Expose private String createdBy;
  @SerializedName("Subject") @Expose private String subject;
  @SerializedName("NoteText") @Expose private String noteText;

  public String getDocumentBody() {
    return documentBody;
  }

  public void setDocumentBody(String documentBody) {
    this.documentBody = documentBody;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getAnnotationId() {
    return annotationId;
  }

  public void setAnnotationId(String annotationId) {
    this.annotationId = annotationId;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getNoteText() {
    return noteText;
  }

  public void setNoteText(String noteText) {
    this.noteText = noteText;
  }
}
