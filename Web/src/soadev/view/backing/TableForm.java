package soadev.view.backing;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.context.AdfFacesContext;

import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.event.SelectionEvent;

import org.apache.myfaces.trinidad.model.RowKeySet;

import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import soadev.domain.Job;

public class TableForm {
    private List<Job> jobList;
    private RichTable jobTable;
    private RichPanelGroupLayout panelGroup1;
    

    public Job getSelectedJob(){
        RichTable table = getJobTable();
        if (table.getEstimatedRowCount()>0){
            return (Job)getJobTable().getSelectedRowData();
        }
        return null;
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<Job> getJobList() {
        if (jobList == null){
            jobList = new ArrayList<Job>();
        }
        return jobList;
    }

    public void setJobTable(RichTable jobTable) {
        this.jobTable = jobTable;
    }

    public RichTable getJobTable() {
        return jobTable;
    }

    public void addJob(ActionEvent actionEvent) {
        Job job = new Job();
        getJobList().add(job);
        RichTable table = getJobTable();
        RowKeySet selection = new RowKeySetImpl();
        selection.add(getJobList().size() -1);
        table.setSelectedRowKeys(selection);
    }

    public void tableRowSelected(SelectionEvent selectionEvent) {
        AdfFacesContext.getCurrentInstance().addPartialTarget(getPanelGroup1());
    }

    public void setPanelGroup1(RichPanelGroupLayout panelGroup1) {
        this.panelGroup1 = panelGroup1;
    }

    public RichPanelGroupLayout getPanelGroup1() {
        return panelGroup1;
    }
}
