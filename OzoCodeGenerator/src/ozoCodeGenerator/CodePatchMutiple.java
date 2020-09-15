package ozoCodeGenerator;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.Random;

/**
 * takes care for right extension of the code.
 * */

public class CodePatchMutiple implements CodePatchIface {


    public CodePatchMutiple(List<List<String>> commands){
        this.commands = commands;
    }


    @Override
    public void write(ContentHandler target) {
        doExtend(target);
    }

    /* list of instructions ie name of functions that should be called */
    private List<List<String>> commands;

    public void doExtend(ContentHandler target){

        if (commands == null ) { System.out.println("PATH SHOULD NOT BE NULL"); }
        //doExtend(commands,0,target);
        GeneralElement ge = doExtendIfStatement(commands);
        ge.construct(target);


    }

    public void extendTest(ContentHandler target){

        String elementName = "TEST";

        //Attributes attr = new Attributes();

        try {
            target.startElement("","",  elementName, null);
        } catch (SAXException e) {
            e.printStackTrace();
        }

        try {
            target.endElement("",  "",elementName);
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }


   /* <block type="controls_if" id="okbuc)ErM5YgpmD0kLYu">
        <mutation elseif="5"></mutation>

        <value name="IF0">
          <block type="procedures_callreturn" id="WZoLy;aa%w*Ud}Mkp[Ah">
            <mutation name="RobotNumberMatch">
              <arg name="TestID"></arg>
            </mutation>
            <value name="ARG0">
              <block type="math_number" id="MHd=30cveFr=gUKO5(5j">
                <field name="NUM">0</field>
              </block>
            </value>
          </block>
        </value>

        <statement name="DO0">
          <block type="procedures_callnoreturn" id="%V1bP6HO9PJQ6R;:Sl.;">
            <mutation name="ENTRYPOINT"></mutation>
            <next>
              <block type="procedures_callnoreturn" id="cQGMDj,Nj-B8)(Bu[v$f">
                <mutation name="ENTRYPOINT"></mutation>
              </block>
            </next>
          </block>
        </statement>

        <value name="IF1">
          <block type="procedures_callreturn" id="tlYXcuDUru!%K?TjCyWl">
            <mutation name="RobotNumberMatch">
              <arg name="TestID"></arg>
            </mutation>
            <value name="ARG0">
              <block type="math_number" id="EYCrtq^8#Idzzl0Ooc-(">
                <field name="NUM">1</field>
              </block>
            </value>
          </block>
        </value>

        <statement name="DO1">
          <block type="procedures_callnoreturn" id="3~}Whxk:~}zY*cSZ|]rN">
            <mutation name="ENTRYPOINT"></mutation>
          </block>
        </statement>

        <value name="IF2">
          <block type="procedures_callreturn" id="lk0]eW{aIpBjgt:{er/E">
            <mutation name="RobotNumberMatch">
              <arg name="TestID"></arg>
            </mutation>
            <value name="ARG0">
              <block type="math_number" id="+~WU9[8;P#i#d7ntT4Xq">
                <field name="NUM">1</field>
              </block>
            </value>
          </block>
        </value>

        <statement name="DO2">
          <block type="procedures_callnoreturn" id="c3-sQOqCB#=i`z2ongM;">
            <mutation name="ENTRYPOINT"></mutation>
          </block>
        </statement>
      </block>*/

    private GeneralElement doExtendIfStatement(List<List<String>> commands){

        /*<block type="controls_if" id="okbuc)ErM5YgpmD0kLYu">
        <mutation elseif="5"></mutation>*/

        GeneralElement block = new GeneralElement("block");
        block.addParam("type","controls_if" );
        block.addParam("id",generateID());
        if (commands.size() > 1){
            GeneralElement mutation = new GeneralElement("mutation");
            mutation.addParam("elseif",(commands.size()-1)+"");
            block.getChilderns().add(mutation);
        }

        for (int i =0; i <commands.size();i++) {
            GeneralElement ifValue = getIfValue(i);
            block.getChilderns().add(ifValue);
            GeneralElement doValue = getIfDo(i,commands.get(i));
            block.getChilderns().add(doValue);

        }

        GeneralElement next = new GeneralElement("next");
        next.getChilderns().add(block);
        return next;
    }

    /*<value name="IF0">
      <block type="procedures_callreturn" id="WZoLy;aa%w*Ud}Mkp[Ah">
        <mutation name="RobotNumberMatch">
          <arg name="TestID"></arg>
        </mutation>
        <value name="ARG0">
          <block type="math_number" id="MHd=30cveFr=gUKO5(5j">
            <field name="NUM">0</field>
          </block>
        </value>

      </block>
    </value>*/
    private GeneralElement getIfValue(int conditionNumber){


        GeneralElement value = new GeneralElement("value");
        value.addParam("name","IF" + conditionNumber);
        {
            GeneralElement block = new GeneralElement("block");
            block.addParam("type", "procedures_callreturn");
            block.addParam("id", generateID());
            {
                GeneralElement mutation = new GeneralElement("mutation");
                mutation.addParam("name", "RobotNumberMatch");

                {
                    GeneralElement arg = new GeneralElement("arg");
                    arg.addParam("name", "TestID");
                    mutation.getChilderns().add(arg);
                }
                block.getChilderns().add(mutation);
                GeneralElement valueArg = addArgNumber(conditionNumber);
                block.getChilderns().add(valueArg);
            }
            value.getChilderns().add(block);
        }

        return value;

    }

    /**
     *             <value name="ARG0">
     *               <block type="math_number" id="MHd=30cveFr=gUKO5(5j">
     *                 <field name="NUM">0</field>
     *               </block>
     *             </value>
     * */
    private GeneralElement addArgNumber(int conditionNumber){
        String elementName = "";

            GeneralElement value = new GeneralElement("value");
            value.addParam("name","ARG0");

            GeneralElement block = new GeneralElement("block");
            block.addParam("type","math_number");
            block.addParam("id", generateID());

            GeneralElementLeaf field = new GeneralElementLeaf("field");
            field.addParam("name","NUM");
            field.SetLeafData(conditionNumber+"");
            block.getChilderns().add(field);

            value.getChilderns().add(block);

            return value;

    }


    /* <statement name="DO2">
   <block type="procedures_callnoreturn" id="c3-sQOqCB#=i`z2ongM;">
     <mutation name="ENTRYPOINT"></mutation>
   </block>
 </statement>*/
    private GeneralElement getIfDo(int conditionNumber,List<String> commands){
        GeneralElement statement = new GeneralElement("statement");
        statement.addParam("name","DO"+conditionNumber);

         statement.getChilderns().add(getExtendEmenent(0,commands));
        return statement;
    }


    /**
     * recursive method, iterates over list of commands, and generates chain of embeded elements.
     *EXAMPLE
     * 		<block type="procedures_callnoreturn" id="F.)=*Mh7rT[Bn8zW]iX0">
     * 			<mutation name="goWorward"></mutation>
     * 			<next>
     * 				<!-- LAST BLOCK -->
     * 				<block type="procedures_callnoreturn" id="ZB9HToEjFAczhHxuzl|0">
     * 				<mutation name="Flash End"></mutation>
     * 				</block>
     * 			</next>
     * 		</block>
     *
     * */
    private GeneralElement getExtendEmenent(int step,List<String> commands){


        // IT IS NOT LAST ELEMENT
        if (step  < commands.size()) {
            //String elementName = "next";

            GeneralElement block = new GeneralElement("block");
            block.addParam("type", "procedures_callnoreturn");
            block.addParam("id", generateID());
            {
                GeneralElement mutation = new GeneralElement("mutation");
                mutation.addParam("name", commands.get(step));
                block.getChilderns().add(mutation);
            }

            step++;
            if (step != commands.size()) {
                GeneralElement next = new GeneralElement("next");
                GeneralElement rest = getExtendEmenent(step, commands);
                if (rest != null) {
                    next.getChilderns().add(rest);
                    block.getChilderns().add(next);
                }
            }


            return block;
        }
        return null;
    }





    /* source characters for id - inspired by ozoblockly */
    String kg="!#$%()*+,-./:;=?@[]^_`{|}~ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private String generateID(){/*Fb()*/
        StringBuilder sb = new StringBuilder(20);
        int a = kg.length();
        Random rnd = new Random();
        for (int i = 0 ; i < 20; i++)
        {
            sb.append(kg.charAt(rnd.nextInt(a)));
        }
        return sb.toString();

    }



}
