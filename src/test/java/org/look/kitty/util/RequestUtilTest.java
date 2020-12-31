package org.look.kitty.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liangkuai
 * @date 2018/10/21
 */
public class RequestUtilTest {

    @Test
    public void parseCooikeHeaderTest() {
//        String header = "jsessionid=123;sessionid=321";
        String header = "jsessionid=123;";
        int index = header.indexOf(";");
        System.out.println(header.charAt(index));
        System.out.println(header.substring(0, index));
        System.out.println(header.substring(index + 1));
        System.out.println(header.substring(index));

        String queryString = "";
        byte[] bs = queryString.getBytes();
        System.out.println(Arrays.toString(bs));
    }


    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int x) { val = x; }
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }

        LinkedList<TreeNode> stackP = new LinkedList<>();
        if (findNode(p.val, root, stackP))
            stackP.push(root);

        LinkedList<TreeNode> stackQ = new LinkedList<>();
        if (findNode(q.val, root, stackQ))
            stackQ.push(root);

        TreeNode res = null;
        TreeNode a;
        TreeNode b;
        while (stackP.peek() != null && stackQ.peek() != null) {
            a = stackP.pop();
            b = stackQ.pop();
            if (a.val == b.val) {
                res = a;
            }
        }
        return res;
    }

    private boolean findNode(int val, TreeNode node, LinkedList<TreeNode> stack) {
        if (node.val == val) {
            return true;
        }

        if (node.left != null && findNode(val, node.left, stack)) {
            stack.push(node.left);
            return true;
        }
        if (node.right != null && findNode(val, node.right, stack)) {
            stack.push(node.right);
            return true;
        }
        return false;
    }

    @Test
    public void test() {
        TreeNode t3 = new TreeNode(3);
        TreeNode t5 = new TreeNode(5);
        TreeNode t1 = new TreeNode(1);
        TreeNode t6 = new TreeNode(6);
        TreeNode t2 = new TreeNode(2);
        TreeNode t0 = new TreeNode(0);
        TreeNode t8 = new TreeNode(8);
        TreeNode t7 = new TreeNode(7);
        TreeNode t4 = new TreeNode(4);
        t3.left = t5;
        t3.right = t1;
        t5.left = t6;
        t5.right = t2;
        t1.left = t0;
        t1.right = t8;
        t2.left = t7;
        t2.right = t4;

        lowestCommonAncestor(t3, t5, t4);
    }
}