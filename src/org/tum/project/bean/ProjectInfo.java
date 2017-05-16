package org.tum.project.bean;

/**
 * Describes all the information needed for a project
 * Including the database name that the project requires
 * Created by Yin Ya on 2017/5/16.
 */
public class ProjectInfo {

    private String projectName;
    private String moduleTableName;
    private String fifoTableName;
    private String fastfifoTabelName;

    public String getDataBankName() {
        return dataBankName;
    }

    private String dataBankName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getModuleTableName() {
        return moduleTableName;
    }

    public void setModuleTableName(String moduleTableName) {
        this.moduleTableName = moduleTableName;
    }

    public String getFifoTableName() {
        return fifoTableName;
    }

    public void setFifoTableName(String fifoTableName) {
        this.fifoTableName = fifoTableName;
    }

    public String getFastfifoTabelName() {
        return fastfifoTabelName;
    }

    public void setFastfifoTabelName(String fastfifoTabelName) {
        this.fastfifoTabelName = fastfifoTabelName;
    }

    public void setDataBankName(String dataBankName) {
        this.dataBankName = dataBankName;
    }

    @Override
    public String toString() {
        return "project info:" + getProjectName() + " " + getDataBankName() + " " + getModuleTableName() + " " + getFifoTableName()
                + " " + getFastfifoTabelName();
    }
}
