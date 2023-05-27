public class SinglyNode {

    public int first;
    public SinglyNode rest;

    public SinglyNode(int f, SinglyNode r) {
        this.first = f;
        this.rest = r;
    }

    public static void evenOdd(SinglyNode lst) {
        if (lst == null || lst.rest == null) {
            return;
        }
        SinglyNode even = lst;
        SinglyNode odd = lst.rest;
        SinglyNode firstOdd = lst.rest;
        while (even.rest != null && odd.rest != null) {
            even.rest = even.rest.rest;
            even = even.rest.rest;
            odd.rest = odd.rest.rest;
            odd = odd.rest.rest;
        }
        even.rest = firstOdd;
    }
}

