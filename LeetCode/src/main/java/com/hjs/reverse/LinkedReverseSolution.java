package com.hjs.reverse;

import com.hjs.ListNode;

/**
 * @author: HuGoldWater
 * @description:
 */
public class LinkedReverseSolution {

    public static void main(String[] args) {
        ListNode listNode = new ListNode(0);
        listNode.add(1);
        listNode.add(2);
        listNode.add(3);
        listNode.add(4);
        listNode.print();
        ListNode listNode1 = listNode.reverseList(listNode);
        System.out.println(listNode1);
    }

    /*public static ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode pre = head;
        ListNode cur = head.getNext();
        pre.setNext(null);
        while (cur != null) {
            ListNode next = cur.getNext();
            cur.setNext(pre);
            pre = cur;
            cur = next;
        }
        return pre;
    }*/


}
