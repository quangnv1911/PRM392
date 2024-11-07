package com.example.pmg302_project.model;

public class Record {
    private int id;
    private String tid;
    private String description;
    private int amount;
    private String when;
    private String bankSubAccId;
    private String corresponsiveAccount;
    private String bankCodeName;

    public Record() {
    }

    public Record(int id, String tid, String description, int amount, String when, String bankSubAccId, String corresponsiveAccount, String bankCodeName) {
        this.id = id;
        this.tid = tid;
        this.description = description;
        this.amount = amount;
        this.when = when;
        this.bankSubAccId = bankSubAccId;
        this.corresponsiveAccount = corresponsiveAccount;
        this.bankCodeName = bankCodeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getBankSubAccId() {
        return bankSubAccId;
    }

    public void setBankSubAccId(String bankSubAccId) {
        this.bankSubAccId = bankSubAccId;
    }

    public String getCorresponsiveAccount() {
        return corresponsiveAccount;
    }

    public void setCorresponsiveAccount(String corresponsiveAccount) {
        this.corresponsiveAccount = corresponsiveAccount;
    }

    public String getBankCodeName() {
        return bankCodeName;
    }

    public void setBankCodeName(String bankCodeName) {
        this.bankCodeName = bankCodeName;
    }
}
