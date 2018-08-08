package com.squareandcube.locationtracking.model;

public class Employee {

    private String empId, empMobile, empPassword,managerid,taskno,Empaddress,Desaddress,deslat,deslong,curlat,curlong;

    public Employee() {
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpMobile() {
        return empMobile;
    }

    public void setEmpMobile(String empMobile) {
        this.empMobile = empMobile;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    public void setEmpPassword(String password) {
        this.empPassword = password;
    }


    public String getManagerid() {
        return managerid;
    }

    public void setManagerid(String managerid) {
        this.managerid = managerid;
    }

    public String getTaskno() {
        return taskno;
    }

    public void setTaskno(String taskno) {
        this.taskno = taskno;
    }

    public String getDesaddress() {
        return Desaddress;
    }

    public void setDesaddress(String desaddress) {
        Desaddress = desaddress;
    }

    public String getEmpaddress() {
        return Empaddress;
    }

    public void setEmpaddress(String empaddress) {
        Empaddress = empaddress;
    }

    public String getDeslat() {
        return deslat;
    }

    public String getDeslong() {
        return deslong;
    }

    public String getCurlat() {
        return curlat;
    }

    public String getCurlong() {
        return curlong;
    }

    public void setDeslat(String deslat) {
        this.deslat = deslat;
    }

    public void setDeslong(String deslong) {
        this.deslong = deslong;
    }

    public void setCurlat(String curlat) {
        this.curlat = curlat;
    }

    public void setCurlong(String curlong) {
        this.curlong = curlong;
    }
}
