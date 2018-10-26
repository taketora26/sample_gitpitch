## (プレ)変位指定について

## わかりやすく

## 解説させてください

2018/10/26 勉強したことを共有する会(大北会) 

池田 健虎

---

自己紹介は割愛しますが、
一点だけ共有
---

<img src="https://github.com/taketora26/sample_gitpitch/blob/master/img/chiba.jpeg?raw=true" width="200">

先週の日曜日に

千葉の**アクアラインマラソン**に走ってきました。

---

タイムは3時間22分

<img src="https://github.com/taketora26/sample_gitpitch/blob/master/img/IMG_2142.JPG?raw=true" width="400">

---

### ベストタイムではないです

* 20キロ過ぎて右ふくらはぎがつりました
* スタートの位置が1万2000人中、9000人目あたりです
* 股ずれで痛かったです


---
11月に**富士山マラソン**がありますので、

## その結果を乞うご期待

(堀越さんも走りますよ)
---

本題です
---

## 変位指定について
## わかりやすく
## 解説させてください
---
まず、ScalaのGenericsを

一言で説明できる方いますか？

---

Javaをやっている人は

### 「あ、Javaでいう総称型ですね」

とか即座に理解されますが、

Javaをやっていない私は

全然わかりませんでした😇

---

## Genericsとは、
 型パラメータを定義するための機能です

---

もう少し詳細にいうと

Genericsとは、
## 型をパラメータとして取る
## クラスやインタフェースなどを
## 定義するための機能です

---

## パラメータ
「外から渡す変動要素」のこと

---

つまり**型パラメータ**は

クラスの定義時には何の型が入るか決まっていないけども、インスタンス化する際に型を決定するクラスやインターフェースの定義です。

---
そしてScalaのGenericsとは
### この型パラメータするための機能です

---

## 変位指定とは

* 型パラメータに + や - の変位アノテーションを付けることで変位指定すること
* 変位指定は受け取るインスタンス(変数への代入)に対する制約
---

#### 型パラメータの変位には3つあります

* 非変[T]
* 共変[+T]
* 反変[-T]

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

共変の一番のメリットは

```Scala
val list:List[Int] = Nil
```

Nilが使えること。

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