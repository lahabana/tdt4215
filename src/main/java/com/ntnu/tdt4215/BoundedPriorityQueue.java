package com.ntnu.tdt4215;

import java.util.PriorityQueue;

public class BoundedPriorityQueue<T> extends PriorityQueue<T> {
	private static final long serialVersionUID = 1L;
	private int maxItems;
    public BoundedPriorityQueue(int maxItems){
        this.maxItems = maxItems;
    }

    @Override
    public boolean add(T e) {
        boolean success = super.add(e);
        if (!success) {
            return false;
        } else if (this.size() > maxItems) {
        	this.remove(this.toArray()[this.size()-1]);
        }
        return true;
    }
}
