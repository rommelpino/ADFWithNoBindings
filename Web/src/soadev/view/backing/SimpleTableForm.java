package soadev.view.backing;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.context.AdfFacesContext;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;

import org.apache.myfaces.trinidad.util.ComponentReference;

import soadev.domain.Job;


public class SimpleTableForm {
    private List<Job> jobList;

    private RichPanelGroupLayout panelGroup1;

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<Job> getJobList() {
        if (jobList == null) {
            jobList = new ArrayList<Job>();
        }
        return jobList;
    }
    private ComponentReference<RichTable> jobTable;

    public void setJobTable(RichTable jobTable) {
        if (this.jobTable == null) {
            this.jobTable =
                    ComponentReference.newUIComponentReference(jobTable);
        }
    }

    public RichTable getJobTable() {
        return jobTable == null ? null : jobTable.getComponent();
    }

    public Job getSelectedJob() {
        RichTable table = getJobTable();
        if (table.getEstimatedRowCount() > 0) {
            return (Job)getJobTable().getSelectedRowData();
        }
        return null;
    }
    
    public void viewSelected(ActionEvent actionEvent) {
        Job selected = getSelectedJob();
        String msg = null;
        if (selected != null) {
            msg = "Selected :" + selected.getJobId() + " " + selected.getJobTitle();
        } else {
            msg = "No selection.";
        }
        FacesMessage fm =
            new FacesMessage(FacesMessage.SEVERITY_INFO, msg, "");
        FacesContext.getCurrentInstance().addMessage(null, fm);
    }

    public void addJob(ActionEvent actionEvent) {
        Job job = new Job();
        getJobList().add(job);
        RichTable table = getJobTable();
        RowKeySet selection = table.getSelectedRowKeys();
        selection.clear();
        selection.add(getJobList().size() - 1);
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

