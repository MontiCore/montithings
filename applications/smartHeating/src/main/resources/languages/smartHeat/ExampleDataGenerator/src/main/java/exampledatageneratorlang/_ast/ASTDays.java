package exampledatageneratorlang._ast;
import java.util.ArrayList;

public class ASTDays extends ASTDaysTOP {

    public String getDaysString(){
        if(isMonday()){
            return "Monday";
        }
        if(isTuesday()){
            return "Tuesday";
        }
        if(isWednesday()){
            return "Wednesday";
        }
        if(isThursday()){
            return "Thursday";
        }
        if(isFriday()){
            return "Fridays";
        }
        if(isSaturday()){
            return "Saturday";
        }
        if(isSunday()){
            return "Sunday";
        }
        return "";
    }

}