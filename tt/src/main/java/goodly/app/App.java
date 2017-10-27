 package goodly.app;

import org.json.simple.parser.ParseException;
import org.dkpro.statistics.agreement.unitizing.*;
import java.io.*;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class App {
	public static void main(String[] args) {
	        UnitizingAnnotationStudy study1 = makeStudy("same_1.json", "same_2.json");	
	        UnitizingAnnotationStudy study2 = makeStudy("different_1.json", "different_2.json");	
		// testStudy();
		UnitizingAnnotationStudy[] studies = {study1, study2};
		for (UnitizingAnnotationStudy study : studies) {
			System.out.println("-----");
			KrippendorffAlphaUnitizingAgreement alpha = new KrippendorffAlphaUnitizingAgreement(study);
			for (Object category : study.getCategories()) {
		    		System.out.print(category + ": ");
		    		System.out.println(alpha.calculateCategoryAgreement((String) category));
			}
		}
	}

	public static UnitizingAnnotationStudy testStudy() {
		UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(2, 100);
		// study.addUnit(<offset>, <length>, <rater>, <category>)
		study.addUnit(5, 2, 2, "A");
		study.addUnit(4, 3, 1, "A");
		study.addUnit(10, 1, 1, "B");
		study.addUnit(10, 3, 2, "B");
		return study;
	}

        public static UnitizingAnnotationStudy makeStudy(String filename1, String filename2) {
		JSONParser parser = new JSONParser();
	        try {     
                    JSONObject a = (JSONObject) parser.parse(new FileReader(new File(filename1)));
                    JSONObject b = (JSONObject) parser.parse(new FileReader(new File(filename2)));
		    
		    JSONObject jsons[] = {a, b};		    
                    UnitizingAnnotationStudy study = new UnitizingAnnotationStudy(2, ((String) a.get("text")).length());
		    for (int i = 0; i < jsons.length; i++) {
			JSONObject json = jsons[i];
			int increment = i;

                        JSONArray runs = (JSONArray) json.get("highlight_taskruns");
		        JSONObject first_run = (JSONObject) runs.get(0);
		        JSONArray highlights = (JSONArray) first_run.get("highlights");
		        JSONObject contributor_obj = (JSONObject) first_run.get("contributor");
                        Long contributor = (Long) contributor_obj.get("id");
                        for (Object o: highlights) {
                            JSONObject highlight = (JSONObject) o;
                            String topic =  highlight.get("topic").toString();
                            JSONArray offsets = (JSONArray) highlight.get("offsets");
			    for (Object of : offsets) {
				JSONArray offset = (JSONArray) of;
                                Long start = (Long) offset.get(0);
                                Long end = (Long) offset.get(1);
				int annotator = increment; // contributor.intValue() + increment;
                                study.addUnit(start.intValue(), end.intValue() - start.intValue(), annotator, topic);
			    }
                        }
		    }
		    return study;
		} catch (FileNotFoundException e) {
            	    e.printStackTrace();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	} catch (ParseException e) {
           	    e.printStackTrace();
        	}
	    	return null;
       }
}
