public class Fibonacci {

    public static int fib2(int n,int f0,int f1) {
        if(n == 0) {
            return f0;
        }
        if(n == 1) {
            return f1;
        }
        return fib2(n-1, f1, f0 + f1);
    }
    public static void main(String[] args) {
        System.out.println(fib2(5, 0, 1));
    }
}