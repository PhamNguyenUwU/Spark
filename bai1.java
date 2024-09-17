// Importing necessary Spark libraries
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

// Set log level to ERROR to reduce log output
spark.sparkContext.setLogLevel("ERROR")

// Create an RDD from a list of tuples
val inputRDD = spark.sparkContext.parallelize(List(("Z", 1), ("A", 20), ("B", 30), ("C", 40), ("B", 30), ("B", 60)))

// Create another RDD from a list of integers
val listRdd = spark.sparkContext.parallelize(List(1, 2, 3, 4, 5, 3, 2))

// Define aggregation functions
def param0 = (accu: Int, v: Int) => accu + v
def param1 = (accu1: Int, accu2: Int) => accu1 + accu2

// Aggregate example with listRdd
println("aggregate : " + listRdd.aggregate(0)(param0, param1))
// Expected Output: aggregate : 20

// Define another set of aggregation functions
def param3 = (accu: Int, v: (String, Int)) => accu + v._2
def param4 = (accu1: Int, accu2: Int) => accu1 + accu2

// Aggregate example with inputRDD
println("aggregate : " + inputRDD.aggregate(0)(param3, param4))

// Phương pháp 2: treeAggregate – action

def param8= (accu:Int, v:Int) => accu + v

def param9= (accu1:Int, accu2:Int) => accu1 + accu2

println("treeAggregate : " + listRdd.treeAggregate(0)(param8, param9))

// Phương pháp 3: fold – action
println("fold : "+listRdd.fold(0){(acc,v) =>
  val sum = acc+v
  sum
})
println("fold : "+inputRDD.fold(("Total",0)){(acc:(String,Int),v:(String,Int))=>
  val sum = acc._2 + v._2
  ("Total",sum)
})
println("min : "+listRdd.min())
println("min : "+inputRDD.min())
println("max : "+listRdd.max())
println("max : "+inputRDD.max())


// Bài 3. Loại các từ trùng nhau trong list
val spark = SparkSession.builder()
  .appName("SparkByExample")
  .master("local")
  .getOrCreate()
val rdd = spark.sparkContext.parallelize(
  List("Germany India USA","USA India Russia","India Brazil Canada China")
)
val wordsRdd = rdd.flatMap(_.split(" "))
val pairRDD = wordsRdd.map((_,1))
pairRDD.foreach(println)

// Bài 4: Đọc file txt, đọc file json, file xml

val multiline_df = spark.read.option("multiline","true").json("src/b.json")
multiline_df.show(false)



import org.apache.spark.sql.SparkSession

// Create a SparkSession
val spark = SparkSession.builder()
  .appName("SparkByExample")
  .master("local")
  .getOrCreate()

// Create an RDD
val rdd = spark.sparkContext.parallelize(
  List("Germany India USA", "USA India Russia", "India Brazil Canada China")
)

// Perform transformations
val wordsRdd = rdd.flatMap(_.split(" "))
val pairRDD = wordsRdd.map((_, 1))

// Print results
pairRDD.foreach(println)
