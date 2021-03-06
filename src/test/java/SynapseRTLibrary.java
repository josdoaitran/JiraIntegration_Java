import com.sun.jersey.core.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.AuthenticationException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/*
SynapseRT library:
https://doc.go2group.com/display/SRT6/synapseRT+REST+API

 */
public class SynapseRTLibrary extends JiraLibrary{
    private static String testPlanAPI = hostJira + "/rest/synapse/latest/public/testPlan/";

    /**
     *
     * @param testCaseID
     * @param testPlanID
     * @throws AuthenticationException
     */

    public static void addTestCaseToTestPlan(String testCaseID, String testPlanID) throws AuthenticationException {
        String auth = new String(Base64.encode(userNameJira + ":" + passWordJira));
        String createIssueData = "{\"testCaseKeys\":[\""+testCaseID+"\"]}";
        String response = invokePostMethod(auth, testPlanAPI+testPlanID+"/addMembers", createIssueData);
        if(response.contains("Success")){
            System.out.println("Added successfully.");
        }else {
            System.out.println("Unable to add TCs: " + testCaseID + "on TestPlan: " + testPlanID);
        }
    }

    /**
     *
     * @param testPlanID
     * @throws AuthenticationException
     */
    public static void addTestCycleToTestPlan(String testPlanID, String testCycleName) throws AuthenticationException, ParseException {
        String auth = new String(Base64.encode(userNameJira + ":" + passWordJira));
        String Data = "{\"name\":\""+ testCycleName +"\"," +
                "\"environment\":\"Firefox\"," +
                "\"build\":\"build 1.0\"," +
                "\"plannedStartDate\":\""+startTestCycle() +"\"," +
                "\"plannedEndDate\":\""+ endTestCycle() +"\"}";
        String issue = invokePostMethod(auth, testPlanAPI+testPlanID+"/addCycle", Data);
        if(issue.contains("Success")){
            System.out.println("Added successfully.");
        }else {
            System.out.println("Unable to add testCycle for TestPlan: " + testPlanID);
        }
    }

    /**
     * @param testPlanID
     * @param cycleName
     * @param action
    - Start
    - Complete
    - Abort
    - Resume
     * @throws AuthenticationException
     */
    public static void updateTestCycleStatus(String testPlanID, String cycleName, String action) throws AuthenticationException {
        String auth = new String(Base64.encode(userNameJira + ":" + passWordJira));
        String response = invokePutMethodNoData(auth, testPlanAPI+testPlanID+"/cycle/"+cycleName+"/wf/"+action);
        System.out.println(response);
        if(response.contains("Success")){
            System.out.println("Added successfully.");
        }else {
            System.out.println("Unable to update test cycle for test plan: " + testPlanID);
        }
    }

    /**
     *
     * @param testCaseID
     * @param status : Passed / Failed
     * @param testPlanID
     * @param cycleName
     * @throws AuthenticationException
     */
    public static void updateTestCaseInTestCycle(String testCaseID, String status, String testPlanID, String cycleName) throws AuthenticationException {
        String auth = new String(Base64.encode(userNameJira + ":" + passWordJira));
        String data = "{\"testcaseKey\":\""+testCaseID+"\"," +
                "\"result\":\""+status+"\"," +
                "\"comment\":\"Updated through REST\"}";
        String response = invokePostMethod(auth, testPlanAPI+testPlanID+"/cycle/"+cycleName+"/updateTestRun",data);
        System.out.println(response);
        if(response.contains("Success")){
            System.out.println("Update successfully.");
        }else {
            System.out.println("Unable to update test case for test plan: " + testPlanID);
        }
    }


    /**
     *
     * @return
     */

    private static String startTestCycle(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now).toString();
    }
    private static String endTestCycle(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now().plusDays(2);
        return dtf.format(now).toString();
    }
}
