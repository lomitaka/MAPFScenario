package mapfScenario.agents;


import graphics.MVPoint;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents Agent or Robot in MapfScenario
 * */
public class Agent implements Serializable {

    /** Agent name */
    public String name;
    /** Agent name */
    public transient Color color;
    /** agent start position */
    public MVPoint start;
    /** agent end position */
    public MVPoint end;

    /** used for serialization */
    private String serColor;
    private Object readResolve() throws ObjectStreamException {
        color = Color.web(serColor);
        return this;
    }
    private Object writeReplace() throws ObjectStreamException{
        serColor = helpout.methods.colorToWebString(color);
        return  this;
    }

    /** agent inner id, use to distinguish various agents */
    private String agentStamp;
    public Agent(){
        agentStamp = getAgentID();
    }

    public Agent(String name, Color c, MVPoint start, MVPoint end,int agentNum){
        this.name = name;
        this.color = c;
        this.start = start;
        this.end = end;
        agentStamp = getAgentID()+ agentNum;
    }
    public Agent(String name, Color c, MVPoint start, MVPoint end){
        this.name = name;
        this.color = c;
        this.start = start;
        this.end = end;
        agentStamp = getAgentID();
    }
    @Override
    public Agent clone(){

        Agent ac= new Agent();
        ac.color = this.color;
        ac.start = this.start;
        ac.name = this.name;
        ac.end = this.end;
        ac.agentStamp= this.agentStamp;
        return ac;
    }


    @Override
    public boolean equals(Object obj) {
        //return super.equals(obj);

        if (obj == null) return false;
        if (obj == this) return true;
        if (!obj.getClass().getName().equals(this.getClass().getName())){
            return false;
        }
        Agent ag = (Agent)obj;

        if (ag.agentStamp.equals(this.agentStamp)) {
            return true;
        } else {
            return false;
        }

    }


    @Override
    public int hashCode() {
        return agentStamp.hashCode();
      /*  int hash = 1;
        hash = this.name.hashCode()*1024;
        if (this.start != null)
        hash = (hash + this.start.hashCode())*200 ;
        if (this.end != null)
        hash = (hash + this.end.hashCode())*10 ;
        if (this.color != null)
        hash = (hash + this.color.hashCode()) ;
        return hash;*/
    }

    /* generates time dependant agent id */
    private String getAgentID(){
        return new SimpleDateFormat("yy-MM-dd-HH-mm-ss").format(new Date());
    }

}
