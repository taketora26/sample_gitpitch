## 変位指定について

## わかりやすく

## 解説させてください

2018/11/10 

Scala関西Summit 2018

池田 健虎

---

<img src="https://pbs.twimg.com/profile_images/813291577682509824/eut9iJB2_400x400.jpg" width="200">

* [@taket0ra1](https://twitter.com/taket0ra1)
* セプテーニ・オリジナル
* 社内外でScalaを教えています

---

ひとつ共有させてください

---?image=img/008.jpg

@color[white](先月)

@color[white](千葉の**アクアラインマラソン**に走ってきました。)

---

## 完走しました

<img src="https://github.com/taketora26/sample_gitpitch/blob/master/img/IMG_2142.JPG?raw=true" width="400">

2回目のフルマラソンでした。

---?image=img/map1.png&size=100% %&color=White

---?image=img/map2.jpg&size=100% %&color=White
---

本題です
---

## 変位指定について
## わかりやすく
## 解説させてください
---

みなさん、

変位指定を普段定義することはありますか？

---

#### 個人的な意見ですが

---

Scalaを勉強し始めた時に、

書物などで登場してきてますが

実際に変位指定を定義して

プロダクト開発を行う機会は少なく

---

ライブラリのソースコードや、

Scalaの言語仕様などで
 
 `共変[+T]`や`下限境界[B >: A]`などの
 
 変位指定を見かけます。

```Scala
sealed abstract class List[+A] extends AbstractSeq[A]
・・・(省略)

  def ::[B >: A] (x: B): List[B] =
    new scala.collection.immutable.::(x, this)
```

* List型の定義

---


今回のスライドでは

### 変位指定がしっくりきていない方に

* 共変、反変、非変とは
* 境界指定(上限境界、下限境界)とは
* なんのためにあるのか？

などを理解できるように解説したいと思います。


---

型パラメータの紹介

---?image=img/amount_water_glass1.png&position=top&size=20%

Scalaの型パラメータ

<br>
<br>

```Scala
class Glass[T](var content:T) {
  def put(t:T):Unit = { content = t }
  def get:T = content
}
```

#### クラス(インターフェース)の定義時に

#### 何の型が入るか決まっていないが

#### インスタンス化する際に型を決定する機能のこと

---?image=img/Drink1.png&position=top&size=70%

<br>
<br>

```scala
class Drink
class Juice extends Drink
class Water extends Drink
```

---?image=img/cup.png&position=top&size=70%

<br>
<br>

```Scala
scala> val waterGlass =  new Glass[Water](new Water)
waterGlass: Glass[Water] = Glass@2251ccd8
```

```Scala
scala> val juiceGlass = new Glass[Juice](new Juice)
juiceGlass: Glass[Juice] = Glass@4fb799d3
```

---

サブタイプについて

---

Scalaは型の親子関係により

スーパークラスにサブクラスのインスタンスを適合できます。

これをサブタイプの関係といいます。

---?image=img/w2d.png&position=top&size=60%

<br>
<br>
<br>

```scala
val drink:Drink = new Water
```
水はドリンクに適合します。

#### (意訳)`Drink`型とみなすことができます。

---?image=img/j2d.png&position=top&size=55%

<br>
<br>

```scala
val drink:Drink = new Juice
```
ジュースもドリンクに適合します。

---?image=img/gass_all.png&position=top&size=75%

<br>
<br>

#### 一方で型パラメータ`Glass[T]`は、
#### デフォルトの状態では、サブタイプの関係がありません。

---?image=img/w2d_ng.png&position=top&size=60%

<br>
<br>
<br>
<br>

```scala
scala> val glassDrink: Glass[Drink] = new Glass[Water](new Water)
 <console>:17: error: type mismatch;

```
WaterがDrinkのサブタイプであっても
Glass[Water]型をGlass[Drink]型に適応することができません。

---?image=img/w2w_ok.png&position=top&size=60%

<br>
<br>
<br>

```scala
scala> val glassWater: Glass[Water] = new Glass[Water](new Water)
glassWater: Glass[Water] = Glass@6874517b
```

もちろん同じ型であれば、適応できます。

---

#### これを変異指定の非変`[T]`といいます。

非変[T]では、サブタイプの関係を

型パラメータで定義したクラス(トレイト)に使うことができません。

同じ型であれば適合できます。

---

#### でも型パラメータで定義したクラスにも

#### サブタイプの関係を利用したいです。

---

その場合共変[+T]することで、

サブタイプの関係を型パラメータで

定義したクラスに使うことができます。

```scala
class Glass[+T](content:T)
```

```scala
scala> val glassDrink: Glass[Drink] = new Glass[Water](new Water)
glassDrink: Glass[Drink] = Glass@59dfd52c
```

Glass[Water]型をGlass[Drink]型に適応できました。

---

逆にサブタイプの関係を型パラメータで

定義したクラスで反転させたい場合は

反変[-T]を使います。


```scala
class Glass[-T](content:T)
```

```scala
scala> val glassWater: Glass[Water] = new Glass[Drink](new Drink)
glassWater: Glass[Water] = Glass@447abc13
```

Glass[Drink]型をGlass[Water]型に適応しています。

---

つまり変異指定とはこのサブタイプの関係を、

型パラメータのクラスで指定する制約です。

* 非変[T]
* 共変[+T]
* 反変[-T]

---

といっても、これだけだとまだ抽象的ですね。

もう少し深掘りましょう。

---

Scalaの多くのimmutableなコレクションは

共変で定義されています。

(Seq、List、Tuple、Option、Either、Try)

```scala
sealed abstract class List[+A] extends AbstractSeq[A]

sealed abstract class Option[+A] extends Product with Serializable 

final case class Success[+T](value: T) extends Try[T] 
```

---

みなさんがよく使っているListも共変です。

共変にすることによって

サブタイプの関係をそのまま使えます。

```scala
scala> val list:List[Any] = List[Int](1,2,3)
list: List[Any] = List(1, 2, 3)

```

List[Int]をList[Any]に適合できます。

---

<img src="https://github.com/taketora26/sample_gitpitch/blob/kansai/img/kaisou.png?raw=true"  width="300">

サブタイプの関係を使えるので、
ボトムタイプのNothing型を全ての型に適応できます。

```scala
case object Nil extends List[Nothing]
```

```scala
scala> val list:List[Int] = Nil

scala> val list:List[String] = Nil
```

---

一方で、非変定義されているコレクションにArrayがあります。

```scala
final class Array[T] extends java.io.Serializable with java.lang.Cloneable

```

---

#### なぜ`Array`は非変なのでしょうか？

```scala
scala> val water:Water = new Water

scala> val arrayWater : Array[Water] = Array(water)

scala> val arrayDrink:Array[Drink] = arrayWater
<console>:16: error: type mismatch;

```
---

Arrayは可変なコレクション(配列)です。

```scala
scala> val arr:Array[Int] = Array(1,2,3)
arr: Array[Int] = Array(1, 2, 3)

scala> arr(0) = 4

scala> arr
res14: Array[Int] = Array(4, 2, 3)
```

---?image=img/arrW.png

---

```scala
val arrayWater:Array[Water] = Array(new Water)

//もし、Array[Water]をArray[Drink]に適合できた場合
val arrayDrink:Array[Drink] = arrayWater

arrayDrink(0) = new Juice 

val water:Water = arrayWater(0) 
// => juice

```

* Arrayはmutableなコレクション
* サブタイプの関係がある場合、要素が変更される可能性が出てくる
* 型安全が壊れてしまう
* 間違った型の要素を取り出す

---

これらの問題が出てくるので

Arrayは非変で作られています。

またミュータブルなリストであるListBufferも同様に非変です。

```scala
final class ListBuffer[A]
```


---

最後に反変について

---

なかなかメリットがわからないと思いますが、

実はみなさんがすでに使っているもので反変の定義があります。

---
FunctionNです

---

```scala
trait Function1[-T1, +R] extends AnyRef
```

* FunctionNは関数を定義するTraitです。
* 通常はシンタックスシュガーを使います

```scala

scala> val f:Function1[Int,String] = (i:Int) =>  "Hello!" * i

scala> f(2)
res3: String = Hello!Hello!

```
上記はIntを引数にとり、Stringを返す関数です。

---

 `Function1`とは
 
 何か引数を渡すと
 
 何かを返してくれる関数です。

---

みなさん、わらしべ長者の話は知っていますか？

---?image=img/wara.png&position=top&size=40%

<br>
<br>
<br>

わらしべ長者は、もともと貧乏だったのですが、

最初に持っていた藁を物々交換して、最終的に大金持ちになる話です

---

* 子連れの母親
  *アブを結んだ藁をみかんへ
* 喉の渇いている商人
　*みかんを上等な織物へ
* 急いでいる侍
　* 織物を馬へ
* 大きな屋敷の主人
  * 馬を屋敷へ

---

### 何かを渡して、何かを得る。

これ関数と同じ構造ですよね。

```scala
val 子連れの母親: アブが結ばれた藁 => みかん
val 喉の渇いている商人: みかん => 上等な織物
val 急いでいる侍: 上等な織物 => 馬
val 大きな屋敷の主人:馬 => 屋敷
```


---

最初のシーンを関数で表します。

```scala
var 大泣きの男の子に手を焼いている母親 : アブが結ばれた藁 => みかん
```

お母さんを関数として表すと、
アブを結んだ藁を受け取り、みかんを返してくれるお母さんです。

(アブが結ばれた藁 => みかん)

このあと出会う商人がみかんを欲しがるので、わらしべ長者はみかんが必要です。

---

では、アブが結ばれた藁を受け取り、果物を返してくれるお母さんはどうでしょうか？。

```scala
class 果物
class みかん extends 果物

val お母さん : アブが結ばれた藁 => 果物
```

このお母さんはバナナや柿を渡してくることもあります。



---

# APPENDIX
---

## REFERENCE(1)

* [[scala][備忘録] Scalaのジェネリックスを学ぶ](http://d.hatena.ne.jp/j5ik2o/20101106/1289028031)
* [第6回 Scala言語を探検する（4）Scalaの型システム](https://tech.nikkeibp.co.jp/it/article/COLUMN/20090106/322252/?ST=develop&P=3)
* [Javaのジェネリクスは「まがい物」ではない](http://kmizu.hatenablog.com/entry/2017/09/24/074904)
* [JavaとScalaとC#のジェネリクス機能比較表](http://kmizu.hatenablog.com/entry/20101109/1289264947)
* [ScalaでListが共変でなければいけない理由](http://kmizu.hatenablog.com/entry/20120810/1344601464)
* [ScalaとKotlin（と昔のJava）のジェネリクスが壊れている理由](http://kmizu.hatenablog.com/entry/2016/09/04/145023)

---
## REFERENCE(2)
* [自分自身の型をパラメータに取るgenericな型を記述する](http://kmizu.hatenablog.com/entry/20080820/1219231895)
* [Scala逆引きレシピ](www.amazon.co.jp/dp/4798125415)
* [Scalaスケーラブルプログラミング第3版](www.amazon.co.jp/dp/4844381490)
* [実践Scala入門](https://gihyo.jp/book/2018/978-4-297-10141-1)