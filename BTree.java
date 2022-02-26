package DSFinal;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;

public class BTree {
    public static void splitNode(String filename, int nodeIndex, int RecordID, int Reference) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        ArrayList<Integer> split1 = new ArrayList<>();
        ArrayList<Integer> split2 = new ArrayList<>();
        ArrayList<Integer> split3 = new ArrayList<>();
        ArrayList<Integer> splitref1 = new ArrayList<>();
        ArrayList<Integer> splitref2 = new ArrayList<>();
        ArrayList<Integer> splitref3 = new ArrayList<>();
        int value = 4;
        int se = (nodeIndex * 9 * 4);
        int se2 = ((nodeIndex + 1) * 9 * 4);
        int se3 = ((nodeIndex + 2) * 9 * 4);
        for (int i = 1; i < 9; i += 2) {
            file.seek(se + value);
            split3.add(file.readInt());
            value += 4;
            file.seek(se + value);
            splitref3.add(file.readInt());
            value += 4;
            file.seek(se + value);
        }
        split3.add(RecordID);
        splitref3.add(Reference);
        Collections.sort(split3);
        Collections.sort(splitref3);
        int len = split3.size() / 2;
        for (int i = 0; i <= len; i++) {
            split1.add(split3.get(i));
            splitref1.add(splitref3.get(i));
        }
        for (int i = len + 1; i < split3.size(); i++) {
            split2.add(split3.get(i));
            splitref2.add(splitref3.get(i));
        }
        file.seek(se2);
        file.writeInt(0);
        int x = 4;
        if (nodeIndex == 1) {
            file.seek(4);
            int m= file.readInt();
            m+=2;
            file.seek(4);
            file.writeInt(m);
            for (int j = 0; j < split1.size(); j++) {
                file.seek(se2 + x);
                file.writeInt(split1.get(j));
                file.writeInt(splitref1.get(j));
                x += 8;
            }
            file.seek(se3);
            file.writeInt(0);
            int y = 4;
            for (int j = 0; j < split2.size(); j++) {
                file.seek(se3 + y);
                file.writeInt(split2.get(j));
                file.writeInt(splitref2.get(j));
                y += 8;
            }
            file.seek(36 + 4);
            int temp = split1.get(split1.size() - 1);
            file.writeInt(temp);
            file.writeInt(nodeIndex + 1);
            int temp2 = split2.get(split2.size() - 1);
            file.writeInt(temp2);
            file.writeInt(nodeIndex + 2);
            for (int i = 5; i < 9; i += 2) {
                int r = (9 + i);
                file.seek(r * 4);
                file.writeInt(-1);
                r++;
                file.seek(r * 4);
                file.writeInt(-1);
            }
        }
        else {
            int de=(nodeIndex*9)+1;
            file.seek(4);
            int m=file.readInt();
            int de2=(m*9)+1;
            for (int j = 0; j < split1.size(); j++) {
                file.seek(de*4);
                file.writeInt(split1.get(j));
                file.writeInt(splitref1.get(j));
                de += 2;
            }
            file.seek((de2-1)*4);
            file.writeInt(0);
            for (int j = 0; j < split2.size(); j++) {
                file.seek(de2 *4);
                file.writeInt(split2.get(j));
                file.writeInt(splitref2.get(j));
                de2+= 2;
            }
            for (int i = 7; i < 9; i += 2) {
                int r = ((9*nodeIndex) + i);
                file.seek(r * 4);
                file.writeInt(-1);
                r++;
                file.seek(r * 4);
                file.writeInt(-1);
            }
            for(int j=2;j<9;j+=2){
                int re=9+j;
                file.seek(re*4);
                int y=file.readInt();
                if(y==nodeIndex){
                    re--;
                    file.seek(re*4);
                    file.writeInt(split1.get(split1.size()-1));
                    re+=2;
                    file.seek(re*4);
                    file.writeInt(split2.get(split2.size()-1));
                    file.writeInt(m);
                    file.seek(4);
                    file.writeInt(-1);
                }
            }
        }
        file.close();
    }

    public void CreateIndexFileFile(String filename, int numberOfRecords, int m) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        int c = 1; //counter
        for (int i = 0; i < numberOfRecords; i++) {
            for (int j = 0; j < 2 * m + 1; j++) {
                if (j == 1) {
                    if (i == numberOfRecords - 1) {
                        file.writeInt(-1);
                    } else {
                        file.writeInt(c);
                        c++;
                    }
                } else {
                    file.writeInt(-1);
                }
            }
        }
        file.close();
    }

    public int InsertNewRecordAtIndex(String filename, int RecordID, int Reference) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        ArrayList<Integer> ref = new ArrayList();
        ArrayList<Integer> arr = new ArrayList();
        ArrayList<Integer> arr2 = new ArrayList();
        int recordNum = 1;
        file.seek(4);
        int Num = file.readInt();
        file.seek(36);
        int t = file.readInt();

        if (t == 1) {
            int size = 36 + 8;
            file.seek(size);
            int a = file.readInt();
            ref.add(a);
            size += 8;
            file.seek(size);
            a = file.readInt();
            ref.add(a);
            file.seek(10 * 4);
            int fr = file.readInt();

            if (fr > RecordID) {
                recordNum = ref.get(0);
            } else {
                recordNum = ref.get(1);
            }
        }
        if (Num == 1) {
            file.seek(36);
            file.writeInt(0);
            file.writeInt(RecordID);
            file.writeInt(Reference);
            file.seek(4);
            int nodeIndex = file.readInt();
            nodeIndex++;
            file.seek(4);
            file.writeInt(nodeIndex);
        } else {
            boolean write = false;
            for (int i = 1; i < 9; i += 2) {
                int index = (recordNum * 9) + i;
                file.seek(index * 4);
                int value2 = file.readInt();
                if (value2 == -1) {
                    for (int k = 1; k < i; k += 2) {
                        int se = (recordNum * 9) + k;
                        file.seek(se * 4);
                        arr.add(file.readInt());
                        se ++;
                        file.seek(se * 4);
                        arr2.add(file.readInt());
                        se ++;
                        file.seek(se * 4);
                    }

                    arr.add(RecordID);
                    arr2.add(Reference);
                    Collections.sort(arr);
                    Collections.sort(arr2);
                    int se2 = (recordNum * 9)+1;

                    for(int h = 0 ; h < arr.size() ; h++){
                        file.seek(se2*4);
                        file.writeInt(arr.get(h));
                        file.writeInt(arr2.get(h));
                        se2+=2;
                    }

                    write = true;
                    break;
                }
                if (write == true) {
                    break;
                }
            }

            if (write == false) {
                file.seek(9 * 4);
                file.writeInt(1);
                splitNode(filename, recordNum, RecordID, Reference);
            }
        }
        return 0;
    }

    void DeleteRecordFromIndex(String filename, int RecordID) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "rw");
        ArrayList<Integer> arr1 = new ArrayList<> (  );
        ArrayList<Integer>arr2 = new ArrayList<> (  );
        ArrayList<Integer>arr3 = new ArrayList<> (  );
        int ref = SearchARecord ( filename,RecordID );
        if(ref != -1){
            int indx = 36*ref;
            for (int i = 1 ; i< 9 ; i+=2){
                file.seek ( (indx + 4));
                int record = file.readInt ();
                if(record == RecordID){
                    file.seek ( (indx + 4));
                    file.writeInt ( -1 );
                    file.writeInt ( -1 );
                }
                indx+=8;
            }
            int indx2 = 36*(ref+1);
            for(int  i = 1 ; i < 9 ; i++){
                file.seek ( indx2 + 4 );
                int record2 = file.readInt ();
                if(record2 != -1) {
                    arr1.add ( record2 );
                }
                indx2+=4;
            }

            int indx4 = 36*(ref);
            for(int  i = 1 ; i < 9 ; i++){
                file.seek ( indx4 + 4 );
                int record2 = file.readInt ();
                if(record2 == -1) {
                    file.seek ( indx4 + 4 );
                    for (int  j = 0 ; j < arr1.size () ; j++){
                        file.writeInt ( arr1.get ( j ) );
                    }
                }
                indx4+=4;
            }

            int indx3 = 36*(ref+1);
            for(int  i = 1 ; i < 9 ; i++){
                file.seek ( indx3 );
                file.writeInt ( -1 );
                indx3+=4;
            }

            int  indx5 = 36;
            for(int  i = 0 ; i < 9 ; i++){
                file.seek (indx5);
                int record = file.readInt ();
                if(record == RecordID){
                    int saveIndex = (int) file.getFilePointer ();
                    file.seek ( indx5 +8 );
                    int value = file.readInt ();
                    file.seek ( indx5 +8 );
                    file.writeInt ( -1 );
                    file.writeInt ( -1 );
                    file.seek ( saveIndex -4 );
                    file.writeInt ( value );
                }
                indx5+=4;
            }

            int value = 4;
            int se = (ref * 9 * 4);
            for (int i = 1; i < 9; i += 2) {
                file.seek(se + value);
                arr2.add(file.readInt());
                value += 8;
                file.seek(se + value);
            }
            int max = Collections.max ( arr2 );
            file.seek ( 36+12 );
            file.writeInt ( max );
        }
        file.close ();
    }

    public void DisplayIndexFileContent(String filename) throws Exception {
        RandomAccessFile file = new RandomAccessFile(filename, "r");
        while (file.getFilePointer() < file.length()) {
            for(int  i = 0 ; i < 9 ; i++){
            System.out.print(file.readInt() + "    ");
            }
            System.out.print ("\n");
        }
        file.close();
    }

    int SearchARecord(String filename, int RecordID) throws IOException {
        RandomAccessFile file = new RandomAccessFile(filename, "r");
        int indx = 9;
        for(int i = 0 ; i < 4 ; i++) {
            for (int j = 1; j < 9; j += 2) {
                file.seek ( (indx+j)*4);
                int record = file.readInt ();
                if (record == RecordID) {
                    int ref = file.readInt ();
                    return ref;
                }
            }
            indx+=9;
        }
        file.close ();
        return -1;
    }

    public static void main(String[] args) throws Exception {
        BTree bt = new BTree();
        String fileName = "file.txt";
        bt.CreateIndexFileFile(fileName, 5, 4);
        System.out.println ("Index file content");
        bt.DisplayIndexFileContent(fileName);
        bt.InsertNewRecordAtIndex(fileName, 5, 40);
        bt.InsertNewRecordAtIndex(fileName, 3, 41);
        bt.InsertNewRecordAtIndex(fileName, 21, 42);
        bt.InsertNewRecordAtIndex(fileName, 9, 43);
        bt.InsertNewRecordAtIndex(fileName, 1, 44);
        bt.InsertNewRecordAtIndex(fileName, 13, 45);
        bt.InsertNewRecordAtIndex(fileName, 2, 46);
        bt.InsertNewRecordAtIndex(fileName, 7, 47);
        bt.InsertNewRecordAtIndex ( fileName,10,48 );
        System.out.println("\n");
        System.out.println ("Index file content after inserting");
        bt.DisplayIndexFileContent(fileName);
        bt.DeleteRecordFromIndex ( fileName,10 );
        System.out.println ("\nIndex file content after deleting 10");
        bt.DisplayIndexFileContent(fileName);
        bt.DeleteRecordFromIndex ( fileName,21 );
        System.out.println ("\nIndex file content after deleting 21");
        bt.DisplayIndexFileContent(fileName);
    }
}