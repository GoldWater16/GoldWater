package com.hjs;

/**
 * @author: HuGoldWater
 * @description:
 */
public class ListNode {

    private int data;
    private ListNode next;

    public ListNode(int val) {    //定义一个有参构造方法
        data = val;
    }

    public void add(int data) {
        ListNode newListNode = new ListNode(data);
        if (this.next == null) {
            this.next = newListNode;
        } else {
            this.next.add(data);
        }
    }

    public ListNode reverseList(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode pre = head;
        ListNode cur = head.next;
        pre.next = null;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = pre;
            pre = cur;
            cur = next;
        }
        return pre;
    }

    public void print() {
        System.out.println(this.data);
        if (next != null) {
            System.out.print("->");
            next.print();
        }
    }
}
