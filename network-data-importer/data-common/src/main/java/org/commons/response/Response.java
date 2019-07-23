package org.commons.response;

import java.text.SimpleDateFormat;
import java.util.*;

public class Response {

    private static final long serialVersionUID = 1L;
    private List<Integer> errorLines;
    private List<Integer> duplicateDataLines;
    private List<Integer> noSuchDataLines;
    private Integer successCount;
    private String operation;

    public Response() {
        duplicateDataLines = new LinkedList<>();
        errorLines = new LinkedList<>();
        noSuchDataLines = new LinkedList<>();
        successCount = 0;
        operation = "";
    }


    public List<Integer> getErrorLines() {
        return errorLines;
    }

    public void setErrorLines(List<Integer> errorLines) {
        this.errorLines = errorLines;
    }

    public void addError(Integer error) {
        errorLines.add(error);
    }

    public List<Integer> getDuplicateDataLines() {
        return duplicateDataLines;
    }

    public void setDuplicateDataLines(List<Integer> duplicateDataLines) {
        this.duplicateDataLines = duplicateDataLines;
    }

    public void addDuplicateData(Integer duplicateData) {
        duplicateDataLines.add(duplicateData);
    }

    public List<Integer> getNoSuchDataLines() {
        return noSuchDataLines;
    }

    public void setNoSuchDataLines(List<Integer> noSuchDataLines) {
        this.noSuchDataLines = noSuchDataLines;
    }

    public void addNoSuchData(Integer noSuchData) {
        noSuchDataLines.add(noSuchData);
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public void addSuccess() {
        successCount++;
    }



    public Map<String, Object> getResponse() {
        Map<String,Object> responseMap = new HashMap<>();

        if(getErrorLines().size()>0 || getDuplicateDataLines().size()>0 || getNoSuchDataLines().size()>0)
        {
            responseMap.put(operation, "Partial Success");
            responseMap.put("Details",responseToMap());
        }
        else
        {
            responseMap.put(operation, "Success");
            responseMap.put("Details",responseToMap());
        }
        return responseMap;
    }

    public Map<String, Object> responseToMap() {
        Map<String, Object> map = new HashMap<>();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        map.put("Time Stamp",formatter.format(date));

        if(errorLines.size()>0)
        map.put("Error in Data",errorLines);
        map.put(operation+" success count",successCount);
        if(duplicateDataLines.size()>0)
            map.put("Multiple Nodes with this data",duplicateDataLines);
        if(noSuchDataLines.size()>0)
            map.put("No Node exists with this data",noSuchDataLines);

        return map;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}

