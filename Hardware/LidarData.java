
class LidarData {

    private String[] distArray;
    private LidarVector[] lidarArray;

    public LidarData(String[] distdata, double direction) {

            distArray = distdata;
            lidarArray = new LidarVector[distArray.length];

            for (int i = 0; i < distArray.length; i++) {
            int dist = Integer.parseInt(distArray[i]);
            if(CarSocket.LIDAR_MIN<=dist&&dist<CarSocket.LIDAR_MAX){}
            lidarArray[i] = new LidarVector(dist, (direction+i*(360/CarSocket.RESOLUTION)));

            }}
    

    public String[] getDistArray() {
        return distArray;
    }

    public LidarVector[] getLidar(){
        return lidarArray;
    }
}


class LidarVector {
    private int distance;
    private double direction;

    public LidarVector(int distance, double direction) {
        this.distance = distance;
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public double getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "LidarVector{" +
                "distance='" + distance + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}