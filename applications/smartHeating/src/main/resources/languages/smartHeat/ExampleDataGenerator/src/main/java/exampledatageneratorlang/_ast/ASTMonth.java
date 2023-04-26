package exampledatageneratorlang._ast;

public class ASTMonth extends ASTMonthTOP {

    public String getMonthString(){
        if(isJanuary()){
            return "January";
        }
        if(isFebruary()){
            return "February";
        }
        if(isMarch()){
            return "March";
        }
        if(isApril()){
            return "April";
        }
        if(isMay()){
            return "May";
        }
        if(isJune()){
            return "June";
        }
        if(isJuly()){
            return "July";
        }
        if(isAugust()){
            return "August";
        }
        if(isSeptember()){
            return "September";
        }
        if(isOctober()){
            return "October";
        }
        if(isNovember()){
            return "November";
        }
        if(isDecember()){
            return "December";
        }

        return "";
    }

}