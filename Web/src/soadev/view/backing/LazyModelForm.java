package soadev.view.backing;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import javax.faces.event.ActionEvent;

import javax.naming.Context;
import javax.naming.InitialContext;

import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.event.QueryEvent;
import oracle.adf.view.rich.event.QueryListener;
import oracle.adf.view.rich.model.AttributeCriterion;
import oracle.adf.view.rich.model.FilterableQueryDescriptor;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetImpl;

import org.apache.myfaces.trinidad.model.SortCriterion;


import soadev.ext.adf.query.AttributeDef;
import soadev.ext.adf.query.FilterableQueryDescriptorGroovyParser;
import soadev.ext.adf.query.FilterableQueryDescriptorImpl;
import soadev.ext.adf.query.QueryLOV;
import soadev.ext.adf.query.SavedSearchDef;
import soadev.ext.adf.query.SearchFieldDef;
import soadev.ext.trinidad.model.LazySortableModel;
import soadev.ext.trinidad.model.SortableFilterableModel;

import src.model.Job;

import src.service.HRFacadeLocal;

public class LazyModelForm {
    private List<Job> jobList;
    private CollectionModel model;
    private RichTable table;
    private RowKeySet selection;
    private FilterableQueryDescriptor descriptor;
    private HRFacadeLocal service;


    public HRFacadeLocal getService() {

        if (service == null) {

            try {
                final Context context = new InitialContext();
                service =
                        (HRFacadeLocal)context.lookup("java:comp/env/ejb/local/HRFacade");

            } catch (Exception ex) {
                //TODO : bubble up exception or put in log file.
                ex.printStackTrace();
            }
        }
        return service;
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
            model = new JobLazySortableModel();
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


    public void tableFilter(QueryEvent event) {
        ((JobLazySortableModel)model).reload();
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

    public class JobLazySortableModel extends LazySortableModel<Job> {

//        public int getRowCount() {
//            if (rowCount == -1) {
//                StringBuilder builder =
//                    new StringBuilder("SELECT o FROM Job o");
//                String criteria =
//                    QueryLOV.parseFilterCriteria(getFilterCriteria(), true);
//                if (!"".equals(criteria)) {
//                    builder.append(" Where");
//                }
//                builder.append(criteria);
//                rowCount =
//                        (int)(long)getService().getResultCount(builder.toString());
//            }
//            return rowCount;
//        }


        public List<Job> load(int first, int pageSize,
                              List<SortCriterion> sortCriteria,
                              List<AttributeCriterion> filterCriteria) {
            StringBuilder builder = new StringBuilder("Select o from Job o");
            String criteria =
                QueryLOV.parseFilterCriteria(filterCriteria, true);
            if (!"".equals(criteria)) {
                builder.append(" Where");
            }
            builder.append(criteria);
            if (sortCriteria != null && !sortCriteria.isEmpty()) {
                builder.append(" order by ");
                int i = 0;
                for (SortCriterion sc : sortCriteria) {
                    if (i++ > 0) {
                        builder.append(", ");
                    }
                    builder.append("o.");
                    builder.append(sc.getProperty());
                    builder.append(" ");
                    builder.append(sc.isAscending() ? "" : "DESC");
                }
            }
            return (List<Job>)getService().queryByRange(builder.toString(),
                                                        null, first, pageSize);
        }

        public int getPageSize() {
            return 100;
        }

        public List<AttributeCriterion> getFilterCriteria() {
            return ((FilterableQueryDescriptorImpl)getDescriptor()).parseFilterCriteria();
        }
    }

}
