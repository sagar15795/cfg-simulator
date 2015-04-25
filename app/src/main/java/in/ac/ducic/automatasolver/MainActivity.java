package in.ac.ducic.automatasolver;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.StringTokenizer;


public class MainActivity extends ActionBarActivity {
    EditText mterminal,mNterminal,mState,mRules,mString;
    Button mSimulator;
    static ArrayList <String> nonTerminal = new ArrayList <String> ();
    static ArrayList <String> Terminal = new ArrayList <String> ();
    static Rules rules[] = new Rules[13];
    static Simulation simulate = new Simulation();
    static CFG cfg[];
    static String grammar[][];
    static String startState = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mSimulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( mString.getText().length() < 1 )
                    //JOptionPane.showMessageDialog(null, "Nothing to simulate.");
                    Toast.makeText(getApplicationContext(),"Nothing to simulate",Toast.LENGTH_LONG).show();
                else{
                    //simulation.setText("");
                    String t = mString.getText().toString();
                    if (!t.endsWith("$"))
                        t = t+"$";
                    simulate(t);
                }
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.action_Go){
            go_to_simulate();

        }
        if(id==R.id.action_Simulate){
            if ( mString.getText().length() < 1 )
                //JOptionPane.showMessageDialog(null, "Nothing to simulate.");
                Toast.makeText(getApplicationContext(),"Nothing to simulate",Toast.LENGTH_LONG).show();
            else{
                //simulation.setText("");
                String t = mString.getText().toString();
                if (!t.endsWith("$"))
                    t = t+"$";
                simulate(t);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void go_to_simulate() {
        String nTerminal = mNterminal.getText().toString();
        String terminal = mterminal.getText().toString();
        String sState = mState.getText().toString();
        String inRules = mRules.getText().toString();
        ArrayList<String> terminalList = new ArrayList <String> ();
        ArrayList <String> nterminalList = new ArrayList <String> ();
        ArrayList <String> tempLeft = new ArrayList <String> ();
        ArrayList <String> tempRight = new ArrayList <String> ();
        boolean error = false;
        nonTerminal.clear();
        if (nTerminal.length() < 1 || terminal.length() < 1 || sState.length() < 1 || inRules.length() < 1){

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Error......");
            alertDialog.setMessage("Please fill all details");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                }
            });
            alertDialog.show();
        }
        else{
            StringTokenizer st1 = new StringTokenizer(nTerminal," ");
            StringTokenizer st2 = new StringTokenizer(terminal," ");

            while(st1.hasMoreTokens()){
                String in = st1.nextToken();
                if (!nterminalList.contains(in))
                    nterminalList.add(in);
            }

            while(st2.hasMoreTokens()){
                String in = st2.nextToken();
                if (!terminalList.contains(in))
                    terminalList.add(in);
            }

            if(!nterminalList.contains(sState)){

                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error......");
                alertDialog.setMessage("Invalid starting state.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                    }
                });
                alertDialog.show();
                error = true;
            }
            else{
                startState = sState;

               // machineD[3].setText(startState);
            }

            int ctr = 0;
            while(ctr < nterminalList.size()){
                nonTerminal.add(nterminalList.get(ctr).toString());
                ctr++;
            }
            ctr = 0;
            while(ctr < terminalList.size()){
                Terminal.add(terminalList.get(ctr).toString());

                ctr++;
            }
            ctr = 0;
            String text = mRules.getText().toString();
            String[] lines = text.split(System.getProperty("line.separator"));
            while (ctr < lines.length) {

                String line = lines[ctr];
                if (!line.contains("=")) {

                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Error......");
                    alertDialog.setMessage("Invalid rule at line "+(ctr+1)+".");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                        }
                    });
                    alertDialog.show();
                    error = true;
                    return;
                }
                else {
                    StringTokenizer a = new StringTokenizer(line, "=");
                    String left = "";
                    String right = "";
                    if (a.hasMoreTokens())
                        left = a.nextToken();
                    if (a.hasMoreTokens())
                        right = a.nextToken();

                    for (int num = 0; num < left.length(); num++) {
                        if (left.charAt(num) != ' ') {
                            if (!nterminalList.contains(String.valueOf(left.charAt(num)))) {
                                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                                alertDialog.setTitle("Error......");
                                alertDialog.setMessage("Invalid non-terminal character at line "+(ctr+1)+".");
                                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                                    }
                                });
                                alertDialog.show();


                                error = true;
                                break;
                            }
                        }
                    }

                    StringTokenizer b = new StringTokenizer(right,"|");
                    while(b.hasMoreTokens()){
                        String tRight = b.nextToken();
                        for (int num = 0; num < tRight.length(); num++) {
                            if (tRight.charAt(num) != ' ' && tRight.charAt(num) != '\n') {
                                if (Character.isUpperCase(tRight.charAt(num))) {
                                    if (!nterminalList.contains(String.valueOf(tRight.charAt(num)))) {

                                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                                        alertDialog.setTitle("Error......");
                                        alertDialog.setMessage("Invalid non-terminal character at line "+(ctr+1)+".");
                                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                                            }
                                        });
                                        alertDialog.show();
                                        error = true;
                                        break;
                                    }
                                }
                                else if (Character.isLowerCase(tRight.charAt(num))) {
                                    if (!terminalList.contains(String.valueOf(tRight.charAt(num))) && tRight.charAt(num) != 'e') {

                                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                                        alertDialog.setTitle("Error......");
                                        alertDialog.setMessage("Invalid terminal character at line "+(ctr+1)+".");
                                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                                            }
                                        });
                                        alertDialog.show();
                                        error = true;
                                        break;
                                    }
                                }

                                else {
                                    if (!terminalList.contains(String.valueOf(tRight.charAt(num)))) {

                                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                                        alertDialog.setTitle("Error......");
                                        alertDialog.setMessage("Invalid terminal character at line "+(ctr+1)+".");
                                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                                            }
                                        });
                                        alertDialog.show();
                                        error = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!error) {
                            String tempL = left.replaceAll(" ", "");
                            String tempR = tRight.replaceAll(" ", "");
                            tempLeft.add(tempL.replaceAll("\n", ""));
                            tempRight.add(tempR.replaceAll("\n", ""));
						        	/*grammar[ctr][0] = tempL.replaceAll("\n", "");
						        	grammar[ctr][1] = tempR.replaceAll("\n", "");*/
                        }

                    } //while

                }
                ctr++;
            }

            if ( !error ){
                ArrayList <String> stateList = new ArrayList <String> ();

                //oRules.setText("");
                grammar = new String[tempLeft.size()][2];
                for(int x = 0; x < tempLeft.size(); x++){
                    grammar[x][0] = tempLeft.get(x).toString();
                    grammar[x][1] = tempRight.get(x).toString();
                }

                createCFG();
                createRules();

                for (int x = 0; x < rules.length; x++){
                    Log.e("Rules of automata",x+1 + ": (" + rules[x].current_state + "," + rules[x].read + "," + rules[x].pop + ") , (" + rules[x].next_state + "," + rules[x].push + ")\n");
                    if (!stateList.contains(rules[x].current_state)){
                        stateList.add(rules[x].current_state);
                    }
                }

                //machineD[0].setText(stateList.toString().substring(1,stateList.toString().length()-1));
                //machineD[1].setText(terminal.replace(' ',','));
                //machineD[2].setText(nonTerminal.toString().substring(1,nonTerminal.toString().length()-1));


            }
        }
       if (error==false){
           mSimulator.setVisibility(View.VISIBLE);
           mString.setVisibility(View.VISIBLE);
           TextView tv=(TextView)findViewById(R.id.textView1);
           tv.setVisibility(View.VISIBLE);
       }else{
           mSimulator.setVisibility(View.GONE);
           mString.setVisibility(View.GONE);
           TextView tv=(TextView)findViewById(R.id.textView1);
           tv.setVisibility(View.GONE);
       }
    }
    private static void createRules() {
        rules = new Rules[cfg.length + 3];
        String t[] = new String[4];
        rules[0] = new Rules(new String[] {"p"," "," ","q",startState});
        rules[1] = new Rules(new String[] {"q","~"," ","q~"," "});
        rules[2] = new Rules(new String[] {"q~"," ","~","q"," "});
        for (int ctr = 0; ctr < cfg.length; ctr++) {
            if (Terminal.contains(String.valueOf(cfg[ctr].right.charAt(0))))
                t[0] = t[2] = "q" + String.valueOf(cfg[ctr].right.charAt(0));
            else
                t[0] = t[2] = "q~";
            t[1] = cfg[ctr].left;
            t[3] = cfg[ctr].right;

            if(t[3].equals("e")){
                t[0] = t[2] = "q~";
                t[3] = " ";
            }

            rules[ctr + 3] = new Rules(new String[] {t[0], " ", t[1], t[2], t[3]});
        }
    }
    private static void createCFG(){
        ArrayList <String> temp = new ArrayList <String> ();
        ArrayList <String> newGL = new ArrayList <String> ();
        ArrayList <String> newGR = new ArrayList <String> ();
        ArrayList <String> tmpp = new ArrayList <String> ();
        ArrayList <String> tmpn = new ArrayList <String> ();
        ArrayList <String> t = new ArrayList <String> ();

        System.out.println("create cfg daw.");
        int ctr = 0, iCtr;

        while(ctr<grammar.length){
            temp.clear();
            int index = 0;
            String var = "";

            while (index < newGL.size() && String.valueOf(grammar[ctr][1].toString().charAt(0)).equals(newGL.get(index).toString())) {
                var = newGR.get(index).toString() + grammar[ctr][1].substring(1);
                temp.add(var);
                index++;
                if (index == newGL.size())
                    index = 0;
            }
            if (var.equals(""))
                temp.add(grammar[ctr][1]);

            iCtr = ctr + 1;

            while(iCtr < grammar.length /*&& grammar[ctr][0].equals(grammar[iCtr][0])*/){
                if (grammar[ctr][0].equals(grammar[iCtr][0])){
                    index = 0;
                    var = "";
                    for (index = 0; index < newGL.size(); index++) {
                        if (String.valueOf(grammar[iCtr][1].toString().charAt(0)).equals(newGL.get(index).toString())) {
                            var = newGR.get(index).toString() + grammar[iCtr][1].substring(1);
                            temp.add(var);
                        }
                    }
                    if (var.equals(""))
                        temp.add(grammar[iCtr][1]);

                    //temp.add(grammar[iCtr][1]);
                    iCtr++;
                    if (iCtr == grammar.length) {
                        for (int index1 = 0; index1 < temp.size(); index1++) {
                            var = "";
                            for (index = 0; index < newGL.size(); index++) {
                                if (String.valueOf(temp.get(index1).toString().charAt(0)).equals(newGL.get(index).toString())) {
                                    var = newGR.get(index).toString() + temp.get(index1).toString().substring(1);
                                    temp.add(var);
                                    temp.remove(index1);
                                }
                            }
                            if (!var.equals(""))
                                index1 = -1;
                        }
                    }
                }
                else
                    iCtr++;

            }


            //process si temp
            String tRule = "";
            tmpp.clear();
            tmpn.clear();
            boolean isRecursive = false;
            for(int j = 0; j<temp.size(); j++){
                if (grammar[ctr][0].equals(String.valueOf(temp.get(j).toString().charAt(0)))){
                    tmpp.add(temp.get(j));
                    isRecursive = true;
                    Random a = new Random();
                    do {
                        char b = (char) (a.nextInt(26) + 65);
                        tRule = String.valueOf(b);
                    } while (newGL.contains(tRule) || grammar[ctr][0].equals(tRule));
                }
                else {
                    tmpn.add(temp.get(j));
                }
            }

            for (int j = 0; j < tmpn.size(); j++) {
                String newR = tmpn.get(j).toString();
                int cIndex = 2;
                t.clear();
                for (int i = j + 1; i < tmpn.size(); i++) {
                    if (tmpn.get(j).toString().startsWith(tmpn.get(i).toString().substring(0, 1).toString())) {
                        if (!t.contains(tmpn.get(j))) {
                            t.add(tmpn.get(j));
                        }
                        if (!t.contains(tmpn.get(i))) {
                            t.add(tmpn.get(i));
                            tmpn.remove(i);
                            i--;
                        }
                    }
                }
                if (!t.isEmpty())
                    tmpn.remove(j);
                for (int f = 1; f < t.size(); f++) {
                    if (t.get(0).toString().startsWith(t.get(f).toString().substring(0, cIndex).toString())) {
                        if (f + 1 >= t.size()){
                            cIndex++;
                            f = 1;
                        }
                    }
                    else
                        break;
                }

                if (!t.isEmpty()) {
                    Random a = new Random();
                    String trule;
                    do {
                        char b = (char) (a.nextInt(26) + 65);
                        trule = String.valueOf(b);
                    } while (newGL.contains(trule) || grammar[ctr][0].equals(trule));
                    newGR.add(t.get(0).toString().substring(0, cIndex - 1) + trule);
                    newGL.add(grammar[ctr][0]);
                    if (!nonTerminal.contains(grammar[ctr][0]))
                        nonTerminal.add(grammar[ctr][0]);

                    for (int i = 0; i < t.size(); i++) {
                        newR = t.get(i).toString().substring(cIndex - 1);
                        if (isRecursive)
                            newR = newR + tRule;
                        newGR.add(newR);
                        newGL.add(trule);
                        if (!nonTerminal.contains(trule))
                            nonTerminal.add(trule);
                    }
                }
                else {
                    newGR.add(newR + tRule);
                    newGL.add(grammar[ctr][0]);
                    if (!nonTerminal.contains(grammar[ctr][0]))
                        nonTerminal.add(grammar[ctr][0]);
                }
            }
            for (int j = 0; j < tmpp.size(); j++) {
                newGR.add(tmpp.get(j).toString().substring(1) + tRule);
                newGL.add(tRule);
                if (!nonTerminal.contains(tRule))
                    nonTerminal.add(tRule);
            }

            if(isRecursive){
                newGR.add(" ");
                newGL.add(tRule);
                if (!nonTerminal.contains(tRule))
                    nonTerminal.add(tRule);
            }
            ctr += temp.size();
        }

        /*for( int x = 0; x<temp.size(); x++){
        	cout("Linked list: "+temp.get(x));
        }
        for( int x = 0; x<newGL.size(); x++){
        	cout("Left: "+newGL.get(x)+" Right: "+newGR.get(x));
        }*/
        cfg = new CFG[newGL.size()];

        for (int x = 0; x < newGL.size(); x++) {
            cfg[x] = new CFG(new String[] {newGL.get(x).toString(),newGR.get(x).toString()});
        }
    }

    void init(){
        mNterminal =(EditText)findViewById(R.id.et1);
        mterminal =(EditText)findViewById(R.id.et2);
        mState =(EditText)findViewById(R.id.et3);
        mRules =(EditText)findViewById(R.id.et4);
        mString =(EditText)findViewById(R.id.et5);
        mSimulator=(Button)findViewById(R.id.bSimulator);
    }
    private  void simulate(String temp) {

        int ctr = 0;
        int sCtr = 0;
        int num = 0;
        boolean symbol = false;
        simulate.stack.clear();
        simulate.rule_num = 0;

        do{
            if ( sCtr==0 ){
                simulate.state = rules[0].current_state;
                simulate.input = temp.toCharArray();
                simulate.stack.push(" ");
            }
            else {
                for (num = 0; num < rules.length; num++) {
                    if (simulate.state.equals(rules[num].current_state) || rules[num].current_state.equals("q~") ) {
                        if (rules[num].pop.equals(simulate.stack.peek()) || rules[num].pop.equals(" ")
                                || rules[num].pop.equals("~") && symbol && String.valueOf(simulate.state.charAt(1)).equals(simulate.stack.peek().toString()))
                            break;

                    }
                }
                if (num == rules.length) {

                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Error......");
                    alertDialog.setMessage("Input string is not accepted according to the rules.");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
// here you can add functions

                        }
                    });
                    alertDialog.show();
                    return;
                }

                if( !rules[num].next_state.equals("q~" )){
                    simulate.state = rules[num].next_state;
                    if ( ( rules[num].next_state.length() != 2 ))
                        symbol = false;
                }
                if ( !rules[num].read.equals(" ") ) {
                    simulate.state = "q" + simulate.input[ctr];
                    symbol = true;
                    ctr++;
                }

                if (!rules[num].pop.equals(" "))
                    simulate.stack.pop();
                if (!rules[num].push.equals(" ")) {
                    String tmp = "";
                    for (int num1 = rules[num].push.length() - 1; num1 >= 0; num1--) {
                        if(rules[num].push.charAt(num1) == '\'') {
                            num1--;
                            tmp = rules[num].push.substring(num1);
                        }
                        else
                            tmp = String.valueOf(rules[num].push.charAt(num1));
                        simulate.stack.push(tmp);
                    }
                }
                simulate.rule_num = num + 1;
            }

            Log.e("Simulater Result",sCtr + 1 + ") " + simulate.state + "\t" + temp.substring(ctr) + "\t" + simulate.stack.toString() + "\t" + simulate.rule_num + "\n"/* + symbol + "\n"*/);

            sCtr++;
        }while( !simulate.state.equals("q$") || !(simulate.stack.peek().equals(" ") && sCtr != 1) );
    }

}

class Rules {
    String current_state = new String();
    String read = new String();
    String pop = new String();
    String next_state = new String();
    String push = new String();

    public Rules(String in[]){
        current_state = in[0];
        read = in[1];
        pop = in[2];
        next_state = in[3];
        push = in[4];
    }
}
class Simulation {
    Stack<String> stack = new Stack <String> ();
    String state;
    char input[];
    int rule_num;
}
class CFG {
    String left;
    String right;

    public CFG(String in[]) {
        left = in[0];
        right = in[1];
    }
}
