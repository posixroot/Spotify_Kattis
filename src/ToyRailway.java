import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kiran on 11/17/15.
 */
public class ToyRailway {

    class Rail {
        String dest;
        int pass;
        Rail(String dest, int pass) {
            this.dest = dest;
            this.pass = pass;
        }
    }

    HashMap<String, Rail> map ;
    String finalString;
    ArrayList<Long> finalIndex;
    long minSwitches = 100000;
    ArrayList<String> indexArr;

    public static void main(String[] args) {
        ToyRailway tr = new ToyRailway();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String s = null;
        int n=0, m;
        int i;

        try {
            s = br.readLine();
            String[] sSplit = s.split(" ");
            n = Integer.parseInt(sSplit[0]);
            m = Integer.parseInt(sSplit[1]);
            i=0;
            while(i<m) {
                String temp = br.readLine();
                String[] tempSplit = temp.split(" ");
                tr.addEdges(n, tempSplit[0], tempSplit[1]);
                i++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        tr.getResult(n);
    }

    private void getResult(int n) {
        ArrayList<Long> indexBT = new ArrayList<>();
        finalIndex = new ArrayList<>();
        indexArr = new ArrayList<>();
        int i=0;
        while(i<=n) {
            indexArr.add("B");
            i++;
        }
        StringBuilder sb = new StringBuilder("");
        long ret = getRecurPath("", true, sb, indexBT);

        if(ret==-1) {
            System.out.println("Not Possible");
        } else {
            if(finalString!=null)
                printAnswer();
            //System.out.println("Answer: " + ret);
        }
    }

    private void printAnswer() {
        int i=0;
        while(i<finalIndex.size()) {
            indexArr.set(finalIndex.get(i).intValue(), String.valueOf(finalString.charAt(i)));
            i++;
        }
        i=1;
        while(i<indexArr.size()) {
            System.out.print(indexArr.get(i));
            i++;
        }
        System.out.println();
    }

    private long getRecurPath(String src, boolean internalTransfer, StringBuilder sb, ArrayList<Long> indexBT) {
        if(src.equals("1A") && internalTransfer) {

            if(minSwitches>sb.toString().length()) {
                //System.out.println("DEBUG String: " + sb.toString());
                finalString = sb.toString();
                minSwitches = sb.toString().length();
                finalIndex.clear();
                finalIndex.addAll(indexBT);
            }
            return 0;
        }
        if(src.isEmpty()) {
            src = "1A";
        }
        long ret = -1;
        if (internalTransfer) {
            long switchNumber = getSwitchNumber(src);
            String srcDir = getDirection(src);
            if(srcDir.equals("A")) {
                //System.out.println("Debug path: " + src + " to internal B and internal C");
                indexBT.add(switchNumber);
                long bpath = getRecurPath(String.valueOf(switchNumber)+"B", !internalTransfer, sb.append("B"), indexBT);
                sb.deleteCharAt(sb.length()-1);
                long cpath = getRecurPath(String.valueOf(switchNumber)+"C", !internalTransfer, sb.append("C"), indexBT);
                sb.deleteCharAt(sb.length()-1);
                indexBT.remove(indexBT.size()-1);
                ret = getMinimunSanitizedPath(bpath, cpath);
            } else {
                //System.out.println("Debug path: " + src + " to internal A");
                ret  = getRecurPath(String.valueOf(switchNumber)+"A", !internalTransfer, sb, indexBT);
            }
            if(ret!=-1) {
                ret+=1;
            }
        } else {
            //non-internal. Railway used.
            if(map.containsKey(src) && map.get(src)!=null) {
                Rail destRail = map.get(src);
                int useCount = destRail.pass;
                if (useCount == 0) {
                    //System.out.println("Debug path: " + src + " to " + destRail.dest);
                    destRail.pass++;
                    if(!getDirection(destRail.dest).equals("A")) {
                        indexBT.add(getSwitchNumber(destRail.dest));
                        ret = getRecurPath(destRail.dest, !internalTransfer, sb.append(getDirection(destRail.dest)), indexBT);
                        sb.deleteCharAt(sb.length()-1);
                        indexBT.remove(indexBT.size()-1);
                    } else {
                        ret = getRecurPath(destRail.dest, !internalTransfer, sb, indexBT);
                    }
                    destRail.pass--;
                } else {
                    ret = -1;
                }
            }
        }
        return ret;
    }

    private String getDirection(String s) {
        return String.valueOf(s.charAt(s.length() - 1));
    }

    private long getMinimunSanitizedPath(long bpath, long cpath) {
        long ret=-1;
        if(bpath==-1 && cpath==-1) {
            ret = -1;
        } else {
            if(bpath==-1 || cpath==-1) {
                if(bpath==-1)
                    ret = cpath;
                if(cpath==-1)
                    ret = bpath;
            } else {
                ret = Math.min(bpath, cpath);
            }
        }
        return ret;
    }

    private void addEdges(int n, String s1, String s2) {
        if(map==null) {
            map = new HashMap<>();
        }
        if(!sanityCheck(n, s1) || !sanityCheck(n, s2)) {
            System.out.println("Error: Invalid switch number.");
            System.exit(0);
        }

        Rail forSrc  = new Rail(s2, 0);
        Rail forDest = new Rail(s1, 0);

        if(map.containsKey(s1) || map.containsKey(s2)) {
            System.out.println("Invalid map/input");
            System.exit(0);
        }
        map.put(s1, forSrc);
        map.put(s2, forDest);
    }

    private boolean sanityCheck(int n, String s) {

        long railSwitch = getSwitchNumber(s);

        return railSwitch > 0 && railSwitch <= n;
    }

    private long getSwitchNumber(String s) {
        long mult = 1;
        long railSwitch=0;
        int i=0;
        while(i<s.length()) {
            if(s.charAt(i)>='0' && s.charAt(i)<='9') {
                //railSwitch = (railSwitch * 10) + (long) (s.charAt(i) - '0');
                railSwitch = (railSwitch * 10) + (long) Character.getNumericValue(s.charAt(i));
            } else {
                break;
            }
            i++;
        }
        return railSwitch;
    }
}
