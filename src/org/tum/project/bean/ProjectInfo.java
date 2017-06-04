package org.tum.project.bean;

/**
 * Describes all the information needed for a project
 * Including the database name that the project requires
 * Created by Yin Ya on 2017/5/16.
 */
public class ProjectInfo {

    private String simulationFile;
    private String moduleTableName;
    private String fifoTableName;
    private String fastfifoTabelName;
    private String dataBankName;

    public String getDataBankName() {
        return dataBankName;
    }



    public String getSimulationFile() {
        return simulationFile;
    }

    public void setSimulationFile(String simulationFile) {
        this.simulationFile = simulationFile;
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
        return "project info:" + getSimulationFile() + " " + getDataBankName() + " " + getModuleTableName() + " " + getFifoTableName()
                + " " + getFastfifoTabelName();
    }
}
