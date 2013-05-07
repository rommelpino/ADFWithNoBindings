package soadev.view.backing;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.event.QueryEvent;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import soadev.domain.Job;
import soadev.domain.JobType;

import soadev.ext.adf.query.AttributeDef;
import soadev.ext.adf.query.FilterableQueryDescriptorGroovyParser;
import soadev.ext.adf.query.FilterableQueryDescriptorImpl;
import soadev.ext.adf.query.SavedSearchDef;
import soadev.ext.adf.query.SearchFieldDef;
import soadev.ext.trinidad.model.SortableFilterableModel;

public class SortableFilterableForm {
    private List<Job> jobList;
    private CollectionModel model;
    private RichTable table;
    private RowKeySet selection;
    private FilterableQueryDescriptor descriptor;


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
            //            model = new MyCollectionModel(getJobList());
            model = new SortableFilterableModel(getJobList());
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

    public void setSelection(RowKeySet selection) {
        this.selection = selection;
    }

    public RowKeySet getSelection() {
        if (selection == null) {
            selection = new RowKeySetImpl();
            selection.add("job2");
        }
        return selection;
    }

    private boolean isCaseInsensitive(String key,
                                      FilterableQueryDescriptor descriptor) {
        Map filterFeatures = descriptor.getFilterFeatures();

        if (filterFeatures != null) {
            Set features = (Set)filterFeatures.get(key);

            return (features != null) &&
                (features.contains(FilterableQueryDescriptor.FilterFeature.CASE_INSENSITIVE));
        }
        return false;
    }

    public void tableFilter(QueryEvent event) {
        FilterableQueryDescriptorGroovyParser.handleFilter(event);
    }

    public void setDescriptor(FilterableQueryDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public FilterableQueryDescriptor getDescriptor() {
        if (descriptor == null) {
            Map<String, AttributeDef> attributes =
                new HashMap<String, AttributeDef>();
            attributes.put("jobId",
                           new AttributeDef("jobId", String.class, null,
                                            AttributeDef.INPUT_TEXT));
            attributes.put("jobTitle",
                           new AttributeDef("jobTitle", String.class, null,
                                            AttributeDef.INPUT_TEXT));
            attributes.put("maxSalary",
                           new AttributeDef("maxSalary", BigDecimal.class,
                                            null, AttributeDef.INPUT_TEXT));
            attributes.put("minSalary",
                           new AttributeDef("minSalary", BigDecimal.class,
                                            null, AttributeDef.INPUT_TEXT));
            attributes.put("jobType.color",
                           new AttributeDef("jobType.color", String.class,
                                            null, AttributeDef.INPUT_TEXT));
            SavedSearchDef ssd = new SavedSearchDef();
            ssd.setName("Table Filter 101");
            ssd.setReadOnly(true);
            ssd.setDefaultSearch(true);
            for (Map.Entry<String, AttributeDef> entry :
                 attributes.entrySet()) {
                AttributeDef attr = entry.getValue();
                SearchFieldDef sfd = new SearchFieldDef();
                sfd.setAttrName(attr.getName());
                sfd.setOperator(attr.getDefaultOperator());
                ssd.addSearchFieldDef(sfd);
            }
            descriptor = new FilterableQueryDescriptorImpl(ssd, attributes);
        }
        return descriptor;
    }

}
