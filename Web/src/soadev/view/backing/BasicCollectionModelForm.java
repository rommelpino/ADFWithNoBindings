package soadev.view.backing;

import groovy.util.Eval;

import java.io.StringWriter;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.Map;

import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichTable;

import oracle.adf.view.rich.event.QueryEvent;
import oracle.adf.view.rich.model.ConjunctionCriterion;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.CollectionModel;

import org.apache.myfaces.trinidad.model.RowKeySet;

import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import soadev.domain.Job;

import soadev.domain.JobType;

import soadev.ext.adf.query.AttributeDef;
import soadev.ext.adf.query.Constants;
import soadev.ext.adf.query.FilterableQueryDescriptorGroovyParser;
import soadev.ext.adf.query.FilterableQueryDescriptorImpl;
import soadev.ext.adf.query.OperatorDef;
import soadev.ext.adf.query.SavedSearchDef;
import soadev.ext.adf.query.SearchFieldDef;

import soadev.ext.trinidad.model.SortableFilterableModel;

import soadev.view.model.MyCollectionModel;

public class BasicCollectionModelForm {
    private List<Job> jobList;
    private CollectionModel model;
    private RichTable table;


    @PostConstruct
    public void init() {
        jobList = new ArrayList<Job>();
        JobType blue = new JobType("blue");
        JobType white = new JobType("white");
        for (int i = 0; i < 500; i++) {
            BigDecimal maxSalary =
                BigDecimal.valueOf(i).multiply(BigDecimal.TEN);
            BigDecimal minSalary =
                maxSalary.add(new BigDecimal("100").negate());
            JobType type = null;
            if (i % 3 == 0) {
                type = blue;
            } else {
                type = white;
            }
            jobList.add(new Job("job" + i, "job Title " + i, maxSalary,
                                minSalary, type));
        }
    }

    public void setJobList(List<Job> jobList) {
        this.jobList = jobList;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public CollectionModel getModel() {
        if (model == null) {
            model = new MyCollectionModel(getJobList());
        }
        return model;
    }

    public void action(ActionEvent actionEvent) {
        Job tableSelected = (Job)getTable().getSelectedRowData();
        System.out.println("selected: " + tableSelected);
        RowKeySet tableSelection = getTable().getSelectedRowKeys();
        Iterator iterator = tableSelection.iterator();
        CollectionModel model = (CollectionModel)table.getValue();
        System.out.println(model.equals(getModel())); //equal
        Object oldKey = model.getRowKey();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            model.setRowKey(key);
            Job job = (Job)model.getRowData();
            System.out.println("job: " + job);

        }
        model.setRowKey(oldKey);
    }


    public void setTable(RichTable table) {
        this.table = table;
    }

    public RichTable getTable() {
        return table;
    }
}
