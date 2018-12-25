package ru.ifmo.ctd.ngp.demo.ffchooser.reinforce.state;

import java.util.LinkedList;

/**
 * Limited queue of doubles that stores and updates the average value of its elements.

 * @author Arina Buzdalova
 */
class LimitedQueue extends LinkedList<Double> {
	private static final long serialVersionUID = -3528682321926864772L;
	private final int limit;	
	private double sum;

    public LimitedQueue(int limit) {
    	if (limit <= 0) {
    		throw new IllegalArgumentException("Limit should be positive.");
    	}
        this.limit = limit;
        this.sum = 0;
    }

    @Override
    public boolean add(Double addon) {
        super.add(addon);
        
        Double removed = null;
        
        while (size() > limit) { 
        	removed = super.remove(); 
        }
        
        if (removed != null) {
        	sum -= removed;
        }
        
        sum += addon;
        
        return true;
    }
    
    public double getAverage() {
    	return sum / size();
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + limit;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		LimitedQueue other = (LimitedQueue) obj;
        return limit == other.limit;
    }
    
    
}