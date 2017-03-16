package com.mcsaatchi.gmfit.architecture.rest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CRMNotesResponseInnerData {
  @SerializedName("status") @Expose private String status;
  @SerializedName("transactionNo") @Expose private Integer transactionNo;
  @SerializedName("noteAttributesLst") @Expose private List<CRMNotesResponseNoteAttribute>
      noteAttributesLst = null;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getTransactionNo() {
    return transactionNo;
  }

  public void setTransactionNo(Integer transactionNo) {
    this.transactionNo = transactionNo;
  }

  public List<CRMNotesResponseNoteAttribute> getNoteAttributesLst() {
    return noteAttributesLst;
  }

  public void setNoteAttributesLst(List<CRMNotesResponseNoteAttribute> noteAttributesLst) {
    this.noteAttributesLst = noteAttributesLst;
  }
}
