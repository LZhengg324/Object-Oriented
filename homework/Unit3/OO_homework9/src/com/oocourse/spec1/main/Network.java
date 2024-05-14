package com.oocourse.spec1.main;

import java.util.HashMap;
import com.oocourse.spec1.exceptions.EqualPersonIdException;
import com.oocourse.spec1.exceptions.EqualRelationException;
import com.oocourse.spec1.exceptions.PersonIdNotFoundException;
import com.oocourse.spec1.exceptions.RelationNotFoundException;

public interface Network {

    /*@ public instance model non_null Person[] people;
      @*/

    /*@ invariant people != null && (\forall int i,j; 0 <= i && i < j && j < people.length; !people[i].equals(people[j]));*/

    //@ ensures \result == (\exists int i; 0 <= i && i < people.length; people[i].getId() == id);
    public /*@ pure @*/ boolean contains(int id);

    /*@ public normal_behavior
      @ requires contains(id);
      @ ensures (\exists int i; 0 <= i && i < people.length; people[i].getId() == id &&
      @         \result == people[i]);
      @ also
      @ public normal_behavior
      @ requires !contains(id);
      @ ensures \result == null;
      @*/
    public /*@ pure @*/ Person getPerson(int id);

    /*@ public normal_behavior
      @ requires !(\exists int i; 0 <= i && i < people.length; people[i].equals(person));
      @ assignable people[*];
      @ ensures people.length == \old(people.length) + 1;
      @ ensures (\forall int i; 0 <= i && i < \old(people.length);
      @          (\exists int j; 0 <= j && j < people.length; people[j] == (\old(people[i]))));
      @ ensures (\exists int i; 0 <= i && i < people.length; people[i] == person);
      @ also
      @ public exceptional_behavior
      @ signals (EqualPersonIdException e) (\exists int i; 0 <= i && i < people.length;
      @                                     people[i].equals(person));
      @*/
    public void addPerson(/*@ non_null @*/Person person) throws EqualPersonIdException;

    /*@ public normal_behavior
      @ requires contains(id1) && contains(id2) && !getPerson(id1).isLinked(getPerson(id2));
      @ assignable people[*];
      @ ensures people.length == \old(people.length);   //保证长度不变
      @ ensures (\forall int i; 0 <= i && i < \old(people.length);
      @          (\exists int j; 0 <= j && j < people.length; people[j] == \old(people[i])));   //保证不更换people中的元素
      @ ensures (\forall int i; 0 <= i && i < people.length && \old(people[i].getId()) != id1 &&
      @     \old(people[i].getId()) != id2; \not_assigned(people[i]));  //保证不修改非此id的Person
      @ ensures getPerson(id1).isLinked(getPerson(id2)) && getPerson(id2).isLinked(getPerson(id1)); //将id1和id2的Person link
      @ ensures getPerson(id1).queryValue(getPerson(id2)) == value; //写入id1 Person的value
      @ ensures getPerson(id2).queryValue(getPerson(id1)) == value; //写入id2 Person的value
      @ ensures (\forall int i; 0 <= i && i < \old(getPerson(id1).acquaintance.length);
      @         not_assigned(getPerson(id1).acquaintance[i],getPerson(id1).value[i]));  //对旧有的acquaintance和value不做赋值
      @ ensures (\forall int i; 0 <= i && i < \old(getPerson(id2).acquaintance.length);
      @         not_assigned(getPerson(id2).acquaintance[i],getPerson(id2).value[i]));  //对旧有的acquaintance和value不做赋值
      @ ensures getPerson(id1).value.length == getPerson(id1).acquaintance.length;  //保证acquaintance长度与value长度相同
      @ ensures getPerson(id2).value.length == getPerson(id2).acquaintance.length;  //保证acquaintance长度与value长度相同
      @ ensures \old(getPerson(id1).value.length) == getPerson(id1).acquaintance.length - 1;    //acquaintance长度比旧有的value长度多1
      @ ensures \old(getPerson(id2).value.length) == getPerson(id2).acquaintance.length - 1;    //acquaintance长度比旧有的value长度多1
      @ also
      @ public exceptional_behavior
      @ assignable \nothing;
      @ requires !contains(id1) || !contains(id2) || getPerson(id1).isLinked(getPerson(id2));
      @ signals (PersonIdNotFoundException e) !contains(id1);
      @ signals (PersonIdNotFoundException e) contains(id1) && !contains(id2);
      @ signals (EqualRelationException e) contains(id1) && contains(id2) &&
      @         getPerson(id1).isLinked(getPerson(id2));
      @*/
    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException;

    /*@ public normal_behavior
      @ requires contains(id1) && contains(id2) && getPerson(id1).isLinked(getPerson(id2));
      @ ensures \result == getPerson(id1).queryValue(getPerson(id2));
      @ also
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id1);
      @ signals (PersonIdNotFoundException e) contains(id1) && !contains(id2);
      @ signals (RelationNotFoundException e) contains(id1) && contains(id2) &&
      @         !getPerson(id1).isLinked(getPerson(id2));
      @*/
    public /*@ pure @*/ int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException;


    /*@ public normal_behavior
      @ requires contains(id1) && contains(id2);
      @ ensures \result == (\exists Person[] array; array.length >= 2;
      @                     array[0].equals(getPerson(id1)) &&
      @                     array[array.length - 1].equals(getPerson(id2)) &&
      @                      (\forall int i; 0 <= i && i < array.length - 1;
      @                      array[i].isLinked(array[i + 1]) == true));
      @ also
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id1);
      @ signals (PersonIdNotFoundException e) contains(id1) && !contains(id2);
      @*/
    public /*@ pure @*/ boolean isCircle(int id1, int id2) throws PersonIdNotFoundException;

    /*@ ensures \result ==
      @         (\sum int i; 0 <= i && i < people.length &&
      @         (\forall int j; 0 <= j && j < i; !isCircle(people[i].getId(), people[j].getId()));
      @         1);
      @*/
    public /*@ pure @*/ int queryBlockSum();

    /*@ ensures \result ==
      @         (\sum int i; 0 <= i && i < people.length;
      @             (\sum int j; i < j && j < people.length;
      @                 (\sum int k; j < k && k < people.length
      @                     && getPerson(people[i].getId()).isLinked(getPerson(people[j].getId()))
      @                     && getPerson(people[j].getId()).isLinked(getPerson(people[k].getId()))
      @                     && getPerson(people[k].getId()).isdLinked(getPerson(people[i].getId()));
      @                     1)));
      @*/
    public /*@ pure @*/ int queryTripleSum();

    public boolean queryTripleSumOKTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData, HashMap<Integer, HashMap<Integer, Integer>> afterData, int result);

}
