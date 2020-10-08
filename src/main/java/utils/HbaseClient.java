package utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.BasicConfigurator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class HbaseClient {
    public static Admin admin;
    public static Configuration conf;
    public static Connection connection;

    public static String dis = "hdfs://master:9000/hbase";

    /**
     * connect hbase
     */
    public static void init(){
        if(conf == null || admin == null || connection == null) {
            conf = GlobalEnv.getConf();
            //conf.set("hbase.zookeeper.property.clientPort", "2181");0
            try {

                connection = ConnectionFactory.createConnection(conf);
                admin = connection.getAdmin();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    /**
     * close connect
     */
    public static void close(){
        try {
            if(admin != null){
                admin.close();
            }
            if(connection != null){
                connection.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 创建表
     * @param tableName
     * @param fields
     * @throws IOException
     */
    public static void createTable(String tableName, String[] fields) throws IOException{
        init();
        TableName tablename =TableName.valueOf(tableName);
        if(admin.tableExists(tablename)){
            System.out.println("table is exists");
            admin.disableTable(tablename);
            admin.deleteTable(tablename);
        }
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        for(String str : fields){
            ColumnFamilyDescriptor columnFamilyDescriptor = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(str)).build();
            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptor);
        }
        admin.createTable(tableDescriptorBuilder.build());
        //close();
    }

    /**
     * 添加数据
     * @param tableName
     * @param row
     * @param fields
     * @param values
     * @throws IOException
     */
    public static void addRecord(String tableName, String row, String[] fields, String[] values) throws  IOException{
        init();

        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(row.getBytes());
        for(int i = 0; i != fields.length ; i++){
            String[] col = fields[i].split(":");
            put.addColumn(col[0].getBytes(), col[1].getBytes(), values[i].getBytes());
        }

        table.put(put);
        table.close();
        //close();
    }
    public static void addRecord(String tableName, String row, String[] fields, byte[][] values) throws IOException{
        init();

        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(row.getBytes());
        for(int i = 0; i != fields.length ; i++){
            String[] col = fields[i].split(":");
            put.addColumn(col[0].getBytes(), col[1].getBytes(), values[i]);
        }

        table.put(put);
        table.close();
        //close();
    }
    /**
     * 显示单列数据
     * @param tableName
     * @param column
     * @throws IOException
     */
    public static void  scanColumn(String tableName, String column) throws IOException{
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan sc = new Scan();
        sc.addFamily((column.getBytes()));
        ResultScanner resultScanner = connection.getTable(TableName.valueOf(tableName)).getScanner(sc);

        for(Result result = resultScanner.next(); result != null; result = resultScanner.next()){
            showCell(result);
        }
        table.close();
        //close();
    }

    /**
     * 修改数据
     * @param tableName
     * @param row
     * @param column
     * @param val
     * @throws IOException
     */
    public static void modifyData(String tableName, String row, String column, String val) throws  IOException{
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(row.getBytes());
        String [] cols = column.split(":");
        if(cols.length==1) {
            put.addColumn(column.getBytes(),"".getBytes() , val.getBytes());

        } else {
            put.addColumn(cols[0].getBytes(), cols[1].getBytes() , val.getBytes());

        }
        table.put(put);
        table.close();
        //close();
    }

    /**
     * 删除表
     * @param tableName
     * @param row
     * @throws IOException
     */
    public static void deleteRow(String tableName, String row) throws IOException{
        try {
            init();
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(Bytes.toBytes(row));
            table.delete(delete);
            table.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        //close();
    }

    public static void showCell(Result result){
        Cell[] cells = result.rawCells();
        for(Cell cell : cells){
            System.out.println("RowName:"+new String(CellUtil.cloneRow(cell))+" ");
            System.out.println("Timetamp:"+cell.getTimestamp()+" ");
            System.out.println("column Family:"+new String(CellUtil.cloneFamily(cell))+" ");
            System.out.println("row Name:"+new String(CellUtil.cloneQualifier(cell))+" ");
            System.out.println("value:"+new String(CellUtil.cloneValue(cell))+" ");
        }
    }

    public static void getData(String tableName)throws  IOException{
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        ResultScanner scanner = table.getScanner(scan);
        for(Result result:scanner) {
            showCell((result));
        }

        //close();
    }

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub
        HbaseClient hbaseClient = new HbaseClient();
        BasicConfigurator.configure();
        boolean flag =true;
        while(flag) {
            System.out.println("------------------------------------------------提供以下功能----------------------------------------------");
            System.out.println("                       1- createTable（创建表  ,提供表名、列族名）                                      ");
            System.out.println("                       2- addRecord （向已知表名、行键、列簇的表添加值）                       ");
            System.out.println("                       3- ScanColumn（浏览表     某一列的数据）                                            ");
            System.out.println("                       4- modifyData（修改某表   某行，某一列，指定的单元格的数据）    ");
            System.out.println("                       5- deleteRow（删除 某表   某行的记录）                                                 ");
            System.out.println("------------------------------------------------------------------------------------------------------------------");
            Scanner scan = new Scanner(System.in);

            Integer choose1=Integer.parseInt(scan.nextLine());

            switch (choose1) {
                case 1: {
                    System.out.println("请输入要创建的表名");
                    String tableName=scan.nextLine();
                    System.out.println("请输入要创建的表的列族个数");
                    int Num=scan.nextInt();
                    String [] fields = new String[Num];
                    System.out.println("请输入要创建的表的列族");
                    /* Scanner scanner = new Scanner(System.in);     scanner.next 如不是全局，即会记得上一次输出。相同地址读入值时*/
                    for(int i = 0; i < fields.length ; i ++) {
                        /*fields[i]=scan.next(); 因为之前没有输入过，所以可以读入新值*/
                        scan = new Scanner(System.in);
                        fields[i]=scan.nextLine();

                    }

                    System.out.println("正在执行创建表的操作");
                    hbaseClient.createTable(tableName,fields);
                    break;
                }
                case 2: {
                    System.out.println("请输入要添加数据的表名");
                    String tableName=scan.nextLine();
                    System.out.println("请输入要添加数据的表的行键");
                    String rowKey=scan.nextLine();
                    System.out.println("请输入要添加数据的表的列的个数");
                    int num =scan.nextInt();
                    String fields[]=new String[num];
                    System.out.println("请输入要添加数据的表的列信息 共"+num+"条信息");
                    for(int i = 0; i < fields.length; i ++)

                    {
                        BufferedReader in3= new BufferedReader(new InputStreamReader(System.in));
                        fields[i] = in3.readLine();
                    }

                    System.out.println("请输入要添加的数据信息 共"+num+"条信息");
                    String values[]=new String[num];
                    for(int i=0;i< values.length;i++)

                    {

                        BufferedReader in2 = new BufferedReader(new InputStreamReader(System.in));
                        values[i] = in2.readLine();
                    }

                    System.out.println("原表信息如下：........\n");
                    hbaseClient.getData(tableName);

                    System.out.println("正在执行向表中添加数据的操作........\n");
                    hbaseClient.addRecord(tableName, rowKey, fields, values);

                    System.out.println("\n添加后的表的信息........");
                    hbaseClient.getData(tableName);

                    break;
                }
                case 3: {
                    System.out.println("请输入要查看数据的表名");
                    String tableName=scan.nextLine();
                    System.out.println("请输入要查看数据的列名");
                    String column=scan.nextLine();
                    System.out.println("查看的信息如下：........\n");
                    hbaseClient.scanColumn(tableName, column);
                    break;
                }
                case 4: {
                    System.out.println("请输入要修改数据的表名");
                    String tableName=scan.nextLine();
                    System.out.println("请输入要修改数据的表的行键");
                    String rowKey=scan.nextLine();
                    System.out.println("请输入要修改数据的列名");
                    String column=scan.nextLine();
                    System.out.println("请输入要修改的数据信息  ");
                    String value=scan.nextLine();

                    System.out.println("原表信息如下：........\n");
                    hbaseClient.getData(tableName);
                    System.out.println("正在执行向表中修改数据的操作........\n");
                    hbaseClient.modifyData(tableName, rowKey, column, value);
                    System.out.println("\n修改后的信息如下：........\n");
                    hbaseClient.getData(tableName);
                    break;
                }
                case 5: {
                    System.out.println("请输入要删除指定行的表名");
                    String tableName=scan.nextLine();
                    System.out.println("请输入要删除指定行的行键");
                    String rowKey=scan.nextLine();

                    System.out.println("原表信息如下：........\n");
                    hbaseClient.getData(tableName);
                    System.out.println("正在执行向表中删除数据的操作........\n");
                    hbaseClient.deleteRow(tableName, rowKey);
                    System.out.println("\n删除后的信息如下：........\n");
                    hbaseClient.getData(tableName);
                    break;
                }
                default: {
                    System.out.println("   你的操作有误 ！！！    ");
                    break;
                }
            }
            System.out.println(" 你要继续操作吗？ 是-true 否-false ");
            flag=scan.nextBoolean();
        }
        System.out.println("   程序已退出！    ");
    }

}

