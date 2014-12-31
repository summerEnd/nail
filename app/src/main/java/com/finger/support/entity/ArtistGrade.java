package com.finger.support.entity;

import java.io.Serializable;

public class ArtistGrade implements Serializable{
   public int from;
   public int to;
   public int value;

    public boolean contains(int score){
        return from<=score&&to>=score;
    }

    @Override
    public String toString() {
        return "ArtistGrade{" +
                "from=" + from +
                ", to=" + to +
                ", value=" + value +
                '}';
    }
}
