import scala.util.{Success, Try}
//# ロゴアイコンの設定
//# logo : img/Sepori_log2.gif


Array

class Glass[-T](val content:T)


class 子供のおもちゃ
class アブが結ばれた藁 extends 子供のおもちゃ
class アブと風車が結ばれた藁 extends アブが結ばれた藁

class 果物
class みかん extends 果物
class 温州みかん extends みかん

var 子連れの母親 : アブが結ばれた藁 => みかん

val 母親A : 子供のおもちゃ => みかん        // Function1[子供のおもちゃ, みかん]
val 母親B : アブが結ばれた藁 => 温州みかん   // Function1[アブが結ばれた藁, 温州みかん]
val 母親C : アブと風車が結ばれた藁 => みかん // Function1[アブと風車が結ばれた藁, みかん]
val 子連れの母親2: アブが結ばれた藁 => 果物        // Function1[アブが結ばれた藁, 果物]

val f:Function1[Int,String] = (i:Int) =>  "Hello!" * i


trait Function1[-T1, +R] extends AnyRef


val pf: PartialFunction[アブが結ばれた藁, みかん] = {
  case x:アブが結ばれた藁 => new みかん
  case x:子供のおもちゃ => new みかん
}

子連れの母親 = 母親A
子連れの母親 = 母親B
子連れの母親 = 母親C
子連れの母親 = 子連れの母親2

class 布
class 上等な織物 extends 布
class 上質な着物 extends 上等な織物

class 馬
class 屋敷

val 大泣きに手を焼いていた男の子の母親_: アブが結ばれた藁 => みかん
val 喉の渇きに苦しんでいる商人: みかん => 上等な織物
val 愛馬が急病で倒れてしまった侍: 上等な織物 => 馬
val 大きな屋敷の主人:馬 => 屋敷



//わらしべ　https://www.irasutoya.com/2016/11/blog-post_953.html

//母　https://www.irasutoya.com/2016/03/blog-post_571.html
//和服を着た女性のイラスト　https://www.irasutoya.com/2015/10/blog-post_22.html

// 赤ちゃんを抱っこしているお母さんのイラスト https://www.irasutoya.com/2014/10/blog-post_781.html


class Drink
class Water extends Drink
class Juice extends Drink

val water = new Water
val juice = new Juice
val arrayWater:Array[Water] = Array(water,water)
val arrayDrink:Array[Drink] = arrayWater
arrayDrink(0) = juice

val water2:Water = arrayWater(0)


1 :: List(1,2)
Try

()
Seq

Option


Success
Failure