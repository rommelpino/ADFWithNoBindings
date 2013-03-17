<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://xmlns.oracle.com/adf/faces/rich" prefix="af"%>
<f:view>
  <af:document id="d1">
    <af:form id="f1">
      <af:panelSplitter id="ps1" orientation="vertical"
                        partialTriggers="pc1:ctb1 pc1:ctb2">
        <f:facet name="first">
          <af:panelCollection id="pc1">
            <f:facet name="menus"/>
            <f:facet name="toolbar">
              <af:toolbar id="t2">
                <af:commandToolbarButton text="Add"
                                         id="ctb1"
                                         actionListener="#{viewScope.tableForm.addJob}"/>
                <af:commandToolbarButton text="Refresh Table"
                                         id="ctb2"/>
              </af:toolbar>
            </f:facet>
            <f:facet name="statusbar"/>
            <af:table value="#{viewScope.tableForm.jobList}" var="row"
                      rowBandingInterval="0" id="t1"
                      binding="#{viewScope.tableForm.jobTable}"
                      rowSelection="single"
                      selectionListener="#{viewScope.tableForm.tableRowSelected}">
              <af:column sortable="false" headerText="Job Id" id="c2">
                <af:outputText value="#{row.jobId}" id="ot2"/>
              </af:column>
              <af:column sortable="false" headerText="Title" id="c4">
                <af:outputText value="#{row.jobTitle}" id="ot3"/>
              </af:column>
              <af:column sortable="false" headerText="Max Salary" id="c1">
                <af:outputText value="#{row.maxSalary}" id="ot4"/>
              </af:column>
              <af:column sortable="false" headerText="Min Salary" id="c3">
                <af:outputText value="#{row.minSalary}" id="ot1"/>
              </af:column>
            </af:table>
          </af:panelCollection>
        </f:facet>
        <f:facet name="second">
          <af:panelGroupLayout id="pgl1" layout="vertical"
                               binding="#{viewScope.tableForm.panelGroup1}">
            <af:panelFormLayout id="pfl1"
                                rendered="#{viewScope.tableForm.selectedJob ne null}">
              <af:inputText label="Job Id" id="it1"
                            value="#{viewScope.tableForm.selectedJob.jobId}"
                            autoSubmit="true"/>
              <af:inputText label="Job Title" id="it2"
                            value="#{viewScope.tableForm.selectedJob.jobTitle}"
                            autoSubmit="true"/>
            </af:panelFormLayout>
          </af:panelGroupLayout>
        </f:facet>
      </af:panelSplitter>
    </af:form>
  </af:document>
</f:view>