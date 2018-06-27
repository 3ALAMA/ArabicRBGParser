/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dependensee;

import java.io.Serializable;

/**
 *
 * @author disooqi
 */
public class Edge implements Serializable{

    public Node source;
    public Node target;
    public String label;
    public int sourceIndex;
    public int targetIndex;
    public boolean visible = false;
    public int height;

    @Override
    public String toString() {
	return label+"["+sourceIndex+"->" + targetIndex+"]";
    }


}
