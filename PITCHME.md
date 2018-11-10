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

<br>

非変[T]ではサブタイプの関係を

型パラメータで定義したクラス(トレイト)に

使うことができません。

同じ型であれば適合できます。

---

#### でも型パラメータで定義したクラスにも

#### サブタイプの関係を利用したい場合もあります

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

#### さらに`List`の共変では下限境界が登場します

```scala
sealed abstract class List[+A] extends AbstractSeq[A]

//省略
  def ::[B >: A] (x: B): List[B] =　new scala.collection.immutable.::(x, this)

```

BはAのスーパー型を意味で、

この返り値 List[B] は、

Listがインスタンス化した要素を返すか、汎化した要素のListを返すことを表しています。

---

```scala
scala> val list = List(2,3,4)
list: List[Int] = List(2, 3, 4)

scala> val listInt = 1 :: list
listInt: List[Int] = List(1, 2, 3, 4)
```

同じ要素(Int)の場合は、

同じ要素(Int)のリスト型が作られる

---

Double型を要素に追加する場合は

IntとDoubleのスーパータイプであるAnyVal型の

Listが作られています。

```scala
scala> val listAnyVal = 0.5 :: listInt
listAnyVal: List[AnyVal] = List(0.5, 1, 2, 3, 4)

```

String型を要素に追加する場合は

IntとStringのスーパータイプであるAny型の

Listが作られます。

```scala
scala> val listAny = "hello" :: listInt
listAny: List[Any] = List(hello, 1, 2, 3, 4)

```

