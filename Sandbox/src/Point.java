final class Point
{
   public final int x;
   public final int y;

   public Point(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   public String toString()
   {
      return "(" + x + "," + y + ")";
   }

   public int getDistance(Point a) {
      return Math.abs(a.x-this.x) + Math.abs(a.y-this.y);
   }
//   public double getDistance(Point a) {
//      return Math.sqrt(Math.pow((this.x - a.x), 2) + (Math.pow((this.y - a.y), 2)));
//   }
   public boolean equals(Object other)
   {
      return other instanceof Point &&
         ((Point)other).x == this.x &&
         ((Point)other).y == this.y;
   }

   public int hashCode()
   {
      int result = 17;
      result = result * 31 + x;
      result = result * 31 + y;
      return result;
   }

   public boolean adjacent(Point p)
   {
      return (x == p.x && Math.abs(y - p.y) == 1) ||
              (y == p.y && Math.abs(x - p.x) == 1);
   }
}
