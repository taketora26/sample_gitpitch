## 変位指定について

## わかりやすく

## 解説させてください

2018/11/10 

Scala関西Summit 2018

池田 健虎

---

<img src="https://pbs.twimg.com/profile_images/813291577682509824/eut9iJB2_400x400.jpg" width="200">

* [@taket0ra1](https://twitter.com/taket0ra1)
* セプテーニ・オリジナル 4年目
* 社内外でScalaを教えています

---

ひとつ共有させてください

---?image=img/008.jpg

@color[white](先月)
@color[white](千葉の**アクアラインマラソン**に走ってきました。)

---?image=img/map1.png&size=100% %&color=White

---?image=img/map2.jpg&size=100% %&color=White


---

## 完走しました

<img src="https://github.com/taketora26/sample_gitpitch/blob/master/img/IMG_2142.JPG?raw=true" width="400">

2回目のフルマラソンでした。
---

本題です
---

## 変位指定について
## わかりやすく
## 解説させてください
---

まず、変位指定を普段定義することはありますか？

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

まず型パラメータを紹介

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

Scalaは型の親子関係により

1つのスーパークラスにサブクラスのインスタンスを適合できます。

#### これをサブタイプ(`Subtype Polymorphis`)
と言います

---?image=img/w2d.png&position=top&size=60%

<br>
<br>
<br>
<br>

```scala
val drink:Drink = new Water
```
水はドリンクに適合します。

#### `Drink`型とみなすことができます。

---?image=img/j2d.png&position=top&size=55%

<br>
<br>

```scala
val drink:Drink = new Juice
```
ジュースもドリンクに適合します。
#### `Drink`型とみなすことができます。


---?image=img/gass_all.png&position=top&size=75%

<br>
<br>

#### 一方で型パラメータ`Glass[T]`では、
#### このままの状態では、サブタイプが使えません

---?image=img/w2d_ng.png&position=top&size=60%

<br>
<br>
<br>
<br>

```scala
scala> val glassDrink: Glass[Drink] = new Glass[Water](new Water)
 <console>:17: error: type mismatch;

```

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

#### これを非変`[T]`といいます。


サブタイプ関係を

型パラメータで持つことができません。

Glass[Water]型をGlass[Drink]型に適応できない。

---

このように、サブタイプ関係を型パラメータで制約したり、
またはそのまま使えたり、サブタイプ関係を逆転させるために

**変異指定**があります。

---

#### 型パラメータの変位にはこの3つあります

* 非変[T]
* 共変[+T]
* 反変[-T]

---

型パラメータでもサブタイプ関係をそのまま使える変異が共変[+T]になり、
多くのコレクションが共変で作られています。

---

ScalaではArrayが非変で作られています。

```scala
final class Array[T] extends java.io.Serializable with java.lang.Cloneable

```

---

Arrayは可変なコレクションです。

```scala
scala> val arr:Array[Int] = Array(1,2,3)
arr: Array[Int] = Array(1, 2, 3)

scala> arr(0) = 4

scala> arr
res14: Array[Int] = Array(4, 2, 3)
```

---

#### なぜ`Array`は非変なのか？

```scala
scala> val water:Water = new Water

scala> val arrayWater : Array[Water] = Array(water,water)

scala> val arrayDrink:Array[Drink] = arrayWater
<console>:16: error: type mismatch;

```
---

```scala
// Array[Water]を作成
val arrayWater:Array[Water] = Array(water,water)

//もし、Array[Water]をArray[Drink]に適合できた場合
val arrayDrink:Array[Drink] = arrayWater

// 要素をアップデートする
arrayDrink(0) = juice 

// しかし、Arrayは可変なコレクションなのでarrayWaterの要素も変更されてしまう

// arrayWaterからWaterの要素を取り出す時に
val water:Water = arrayWater(0) 

```

Arrayが可変(mutable)なコレクションであるため、

サブタイプがそのまま使えると、要素が変更されて

要素を取り出す際に型が変わっていることが発生してしまう。

(型安全が壊れる)

そしてこの型安全が壊れていることにコンパイラが気づけない。



---



---


そしてフルーツであれば何でも入れて良いダンボールの制約や
りんご用のダンボールにはりんごのみしか入れられないようにする制約を

型パラメータでは**変異指定**と呼びます。

---

#### 型パラメータの変位には3つあります

* 非変[T]
* 共変[+T]
* 反変[-T]

---


### 非変[T]
りんごのダウンボール用のダンボールはりんご用のダウンボールとしか使えないこと


```Scala
class Fruits
class Apple extends Fruits
class Banana extends Fruits
```

```Scala
class Box[T](var value:T) {
  def put(t:T):Unit = { value = t }
  def get:T = value
}

```
---

```Scala
scala> val appleBox = new Box[Apple](new Apple)
appleBox: Box[Apple] = Box@4364e670

//フルーツ用のダンボールだと型を指定している箇所に、りんご用のダンボールを設定できない
scala> val fruitsBox:Box[Fruits] = appleBox
<console>:14: error: type mismatch;
 found   : Box[Apple]
 required: Box[Fruits]
:Note Apple <: Fruits, but class Box is invariant in type T.
You may wish to define T as +T instead. (SLS 4.5)
       val fruitsBox:Box[Fruits] = appleBox
                                   ^
 ```
 

---

## 今日はここまで。。。。！

---

## 変位指定とは

* 型パラメータに + や - の変位アノテーションを付けることで変位指定すること
* 変位指定は受け取るインスタンス(変数への代入)に対する制約
---
## 非変[T]とは？

* 型が厳格な定義です。その型のインスタンスしか変数に代入できません。
* 基本的に型パラメータを使うときに変異指定をつけない場合は非変です。
* Array、Setが非変です。

---

```Scala
class Fruits
class Apple extends Fruits
class Banana extends Fruits
```

```Scala
class Box[T](var value:T) {
  def put(t:T):Unit = { value = t }
  def get:T = value
}

```

```Scala
scala> val appleBox = new Box[Apple](new Apple)
appleBox: Box[Apple] = Box@4364e670

//スーパークラスの変数に サブクラスを代入できない
scala> val fruitsBox:Box[Fruits] = appleBox
<console>:14: error: type mismatch;
 found   : Box[Apple]
 required: Box[Fruits]
:Note Apple <: Fruits, but class Box is invariant in type T.
You may wish to define T as +T instead. (SLS 4.5)
       val fruitsBox:Box[Fruits] = appleBox
                                   ^
 ```
---


```scala
scala> List("🍺", "☕", "🍸")
res3: List[String] = List(🍺, ☕️, 🍸)
```


### ここで一点注意

* 変位指定は受け取るインスタンスに対する制約
* メソッドの型パラメータに対しては制約することはできない。

```Scala
scala>  val fruitsBox:Box[Fruits] = new Box[Fruits](new Fruits)
fruitsBox: Box[Fruits] = Box@15bf5fd0
scala> fruitsBox.put(new Fruits)

scala> fruitsBox.put(new Apple)

scala> fruitsBox.put(new Banana)

scala> fruitsBox.get
res4: Fruits = Banana@26f8c8d9
```
---

スーパークラスにインスタンのメソッドの引数に、サブクラスのインスタンスを入れることができる。

```Scala
scala>  val fruitsBox2:Box[Fruits] = fruitsBox 
fruitsBox2: Box[Fruits] = Box@15bf5fd0

scala> fruitsBox2.get
res5: Fruits = Banana@26f8c8d9
```

---

しかし非変の特徴として、immutableな場合にこの制限だと強過ぎる。

```Scala
class Box[T](val value:T) {
  def get:T = value
}
```
一旦appleBoxインスタンスが作られたあとは、インスタンスに対する書き込みが行われないので、変数にも代入して良くない？

---

## 共変[+T]とは？
* 型をゆるめる定義です。 
 * サブクラスを代入することができるようになり、スーパークラスの変数にスーパーを継承した色んなサブクラスを代入できます。
* Option、Try、Either、Traversable(Seq、Mapなど)

---

```Scala
class Fruits
class Apple extends Fruits
```

```Scala
class Box[+T](value:T) {
  def get:T = value
}
```

```Scala
scala> val appleBox = new Box[Apple](new Apple)
appleBox: Box[Apple] = Box@5ad7d599

//スーパークラスの変数に サブクラスを代入できる
scala> val fruitsBox:Box[Fruits] = appleBox
fruitsBox: Box[Fruits] = Box@5ad7d599
```
---

Listなので共変の一番のメリットは
全てのクラスのサブクラスであるNothingを持った型であるNilが使えること。


```Scala
val list:List[Int] = Nil
```

```Scala
case object Nil extends List[Nothing]
```

<img src="https://github.com/taketora26/sample_gitpitch/blob/master/img/kaisou.png?raw=true" width="400">
* Scalaのクラス階層

---

ここで一点注意
---
共変はそのままの状態ですと引数に持ってくることができません。

```Scala
class Box[+T](var value:T) {

  def put(t:T):Unit = { value = t }
  def get:T = value
}
```

```Scala
<console>:11: error: covariant type T occurs in contravariant position in type T of value value_=
       class Box[+T](var value:T) {
                         ^
<console>:13: error: covariant type T occurs in contravariant position in type T of value t
         def put(t:T):Unit = { value = t }
                 ^
```
---
これはなぜか？

仮にコンパイルでエラーが出ないとしたときに、

---

日本円を受け取るリンゴ商人が居ます。 (円 => リンゴ)
いつもは元気にリンゴを売ってくれますが今日はおやすみです。
どうしてもリンゴが食べたいあなたは代わりの人を探します。

日本円を受け取る果物商人は代わりになるでしょうか？ (円 => 果物)
この人はナシやミカンを渡してくることもあります。
あなたはリンゴが食べたいのでこの人は代わりになりません。

日本円を受け取る紅玉商人はどうでしょう？ (円 => 紅玉)
紅玉はリンゴなのであなたの欲求は満たされます。

世界各国の通貨を受け取るリンゴ商人はどうでしょう？ (各国通貨 => リンゴ)
あなたは日本円を渡してリンゴが食べられるのでハッピーです。

500円玉しか受け取らないリンゴ商人はどうでしょう？ (500円玉 => リンゴ)
ちょうど財布の中に500円玉がなかったあなたはリンゴを食べられません。

円はサブタイプになる分にはOK(引数は広まる分にはOK)
りんごは

---

```scala

class 通貨
class 円 extends 通貨
class 五百円 extends 円

class 果物
class りんご extends 果物
class 紅玉 extends りんご

var りんご商人 : 円 => りんご
val 果物商人: 円 => 果物
val 紅玉商人: 円 => 紅玉
val 世界りんご商人: 通貨 => りんご
val 五百円りんご商人:五百円 => りんご

// Function1[-T1, +R]
りんご商人 = 果物商人
りんご商人 = 紅玉商人
りんご商人 = 世界りんご商人
りんご商人 = 五百円りんご商人

//イコールが通るサブタイプである
//円はサブタイプになる分にはOK(引数は広まる分にはOK) 円で買えれば良い
//りんごは(狭まる分にはOK) りんごが欲しい(反変)

//FunctionNの引数が反変、引数が共変の理由

//Function1物語
//


//https://www.scala-lang.org/api/2.12.7/scala/Function1.html

```



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