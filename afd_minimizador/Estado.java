/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package afd_minimizador;

import java.util.ArrayList;

/**
 *
 * @author josel_0v10him
 */
public class Estado {
    public ArrayList<Arista> to;
    public String name;
    public boolean initialState;
    public boolean finalState;
    
    public int id;
    public int position_x;
    public int position_y;
    public Estado(String name,int id,boolean initialState,boolean finalState,int x, int y){
        to= new ArrayList<>();
        this.id=id;
        this.initialState=initialState;
        this.finalState=finalState;
        this.name=name;
        position_x=x;
        position_y=y;
    }
    
    public Estado(){
        this.name="";
        position_x=0;
        position_y=0;
    }
    public void AddTransition(Arista transition){
        if(transition==null)return;
         to.add(transition);
    }
    public void print(){
        System.out.println("Name: "+name+" position x: "+position_x+" position y: "+position_y);
        System.out.println("Transition for: "+name);
        to.forEach((transition) -> {
                System.out.println("Transition from "+name+
                        " to q"+transition.to+" with "+transition.entradas);
        });
        System.out.println("Initial: "+ initialState+" Final: "+finalState);
    }
}
