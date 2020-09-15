package ozoCodeGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** takes care of reading result of parsing file. */
public class ActionFileWorker {


    public List<List<String>> loadSolutionFile(File file){

        List<List<String>> resultMultiple = new ArrayList<>();

        String line = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            //line = br.readLine();
            //if (agentCount < 0) { return null;}
            int lastAgentId = -1;
            List<String> result = new ArrayList<>();
            while ((line = br.readLine()) != null){

                if (line.length() == 0) { continue;}
                if (line.trim().charAt(0) == '#') { continue;}
                String[] solData = line.split(" ",6);

                if (solData.length < 5) {
                    System.out.print("unexpected file end");
                    return null; }

                int agentNo = Integer.parseInt(solData[0]);

               // int vertexNo = Integer.parseInt(solData[1]);
                String action = solData[3];


                if (lastAgentId == -1) { lastAgentId = agentNo;}

                //flush agents
                if (lastAgentId != agentNo){
                    resultMultiple.add(result);
                    result= new ArrayList<>();
                    lastAgentId = agentNo;
                }

                result.add(action);
            }

            if (lastAgentId == -1){
                System.out.print("solution do not contains any object");
                br.close();
              return null;
            }
            resultMultiple.add(result);

            br.close();

            return resultMultiple;

        } catch (IOException e) {
        System.out.print("cant read agentfile " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }






}
