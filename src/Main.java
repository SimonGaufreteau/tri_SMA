public class Main {
    public static void main(String[] args) {
        Runner runner = new Runner();
        try{
            runner.run(100000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
