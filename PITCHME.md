## (Pre)変位指定について

## わかりやすく

## 解説させてください

2018/10/31 

Scala勉強会第231回 in 西新宿

池田 健虎

---

<img src="https://pbs.twimg.com/profile_images/813291577682509824/eut9iJB2_400x400.jpg" width="200">

* [@taket0ra1](https://twitter.com/taket0ra1)
* セプテーニ・オリジナル

---

ひとつ共有させてください

---?image=img/008.jpg

@color[white](先月)
@color[white](千葉の**アクアラインマラソン**に走ってきました。)

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

(個人的な意見ですが)

---

Scalaを勉強し始めた時に、

本で登場してきてますが

実際に変位指定を定義してプロダクト開発を行う機会は少なく

---

ライブラリのソースコードや、Scalaの言語仕様などで `共変[+T]`や`下限境界[B >: A]`などの変位指定を見かけます。

* List型の定義

```Scala

sealed abstract class List[+A] extends AbstractSeq[A]
・・・(省略)

  def ::[B >: A] (x: B): List[B] =
    new scala.collection.immutable.::(x, this)
```

---


今回のスライドでは

Scalaの変位指定がしっくりきていない方に

* 共変、反変、非変とは
* 境界指定(上限境界、下限境界)とは
* なんのためにあるのか？
* 変位指定、境界指定の定義について

などを理解できるように解説したいと思います。


---

まず型パラメータを紹介

---

<img src="https://github.com/taketora26/sample_gitpitch/blob/rpscala/img/amount_water_glass1.png?raw=true" width="100">

Scalaの型パラメータ

```Scala
class Glass[T](var content:T) {
  def put(t:T):Unit = { content = t }
  def get:T = content
}
```

#### クラス(インターフェース)の定義時には何の型が入るか決まっていないが
#### インスタンス化する際に型を決定する機能のこと

---

```Scala
class Fruits
class Apple extends Fruits
```

```Scala
scala> val appleGlass = new Glass[AppleJuice](new AppleJuice)
appleGlass: Glass[AppleJuice] = Glass@4381efab

scala> val juiceGlass =  new Glass[Juice](new Juice)
juiceGlass: Glass[Juice] = Glass@31b6938e

```

---

続いて、変位指定について

---

変位指定は以下の3つがあります

* 非変[T]
* 共変[+T]
* 反変[-T]

---

* 非変[T]

<img src="https://github.com/taketora26/sample_gitpitch/blob/rpscala/img/amount_water_glass1.png?raw=true" width="200">

```Scala
class Glass[T](var content:T) {
  def put(t:T):Unit = { content = t }
  def get:T = content
}
```

---

```scala



```



---




Javaをやっている人は

### 「あ、ジャバでいう総称型ですね」

とか即座に理解されますが、

Javaをやっていない私は

全然わかりませんでした😇

---

Genericsとは

### 汎用的なクラスやメソッドを
### 特定の型に対応づける機能のこと

(Java SE5.0 から導入されました。)

---

ジェネリクスってなんのためにあるの？

---

<img src="https://raw.githubusercontent.com/taketora26/sample_gitpitch/itpro/img/nouka.png?raw=true" width="200">

あなたは果物農家で、収穫した果物を果物屋さんやりんごジュース屋さんに配送しています。

---

<img src="https://raw.githubusercontent.com/taketora26/sample_gitpitch/itpro/img/takuhai_yasai_box.png?raw=true" width="150">

収穫した果物を配送するために

**ダンボール箱**を使っていました。

* ジュース屋さんにはりんごが入ったダウンボールのみ
* 果物屋さんには果物が入ったダンボールであればなんでも受け取ってくれました。


---

ジェネリクスが生まれる前までは

* りんご用のダンボール
* バナナ用にダンボール
* フルーツ用のダンボール

を**それぞれダンボール独自に作っていました。**
---

それぞれの果物に対応したダンボールを作るのは、ちょっと手間。

```Scala

//りんご用のダンボール
class Box[Apple](var value: Apple) {
  def put(apple:Apple):Unit = { value = apple }
  def get: Apple = value
}

//フルーツ用のりんご
class Box[Fruits](var value: Fruits) {
  def put(fruits: Fruits):Unit = { value = fruits }
  def get: Fruits = value
}

```

```Scala



```


---

### でもやっていることはダンボールに果物を入れて適切な場所に配送することです。


---
ここでジェネリクスが導入されました。

```Scala
class Box[T](var value:T) {
  def put(t:T):Unit = { value = t }
  def get:T = value
}
```
---

ジェネリクスのおかけで

りんご用かバナナ用か、フルーツ用かは決めずに
ダンボール箱を用意できる体制にできました。

```Scala
scala> val appleBox = new Box[Apple](new Apple)
appleBox: Box[Apple] = Box@37e22cfb

scala> val fruitsBox = new Box[Fruits](new Fruits)
fruitsBox: Box[Fruits] = Box@6ab5f533

```
---

つまりジェネリクスとは

#### クラスの定義時には何の型が入るか決まっていないけども、
#### インスタンス化する際に型を決定する機能のこと
---

Scalaではこのことを

**型パラメータ(parameterized types)** と呼びます。

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