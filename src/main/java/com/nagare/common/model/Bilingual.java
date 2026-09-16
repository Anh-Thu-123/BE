package com.nagare.common.model;

/** Noi dung song ngu Viet - Nhat luu ngay tai cho, dung nhat quan trong toan bo he thong. */
public class Bilingual {
    private String vi;
    private String ja;

    public Bilingual() {}

    public Bilingual(String vi, String ja) {
        this.vi = vi;
        this.ja = ja;
    }

    public String getVi() { return vi; }
    public void setVi(String vi) { this.vi = vi; }
    public String getJa() { return ja; }
    public void setJa(String ja) { this.ja = ja; }
}
