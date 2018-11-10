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

スーパークラスにサブクラスのインスタンスを適合できます。

---?image=img/w2d.png&position=top&size=60%

<br>
<br>
<br>
<br>

```scala
val drink:Drink = new Water
```
水はドリンクに適合します。

#### (意訳すると)`Drink`型とみなすことができます。

---?image=img/j2d.png&position=top&size=55%

<br>
<br>

```scala
val drink:Drink = new Juice
```
ジュースもドリンクに適合します。
#### (意訳すると)`Drink`型とみなすことができます。


---?image=img/gass_all.png&position=top&size=75%

<br>
<br>

#### 一方で型パラメータ`Glass[T]`は、
#### デフォルトの状態では、サブタイプの関係が使えません

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

サブタイプの関係を

型パラメータで使うことができません。

同じ型であれば適合できます。

---

そして変異指定はこのサブタイプの関係を型パラメータで指定する

---
型パラメータでサブタイプの関係をそのまま使いたい場合には

共変[+]を使います。


**変異指定**と呼びます。

---

#### 変位指定にはこの3つあります

* 非変[T]
* 共変[+T]
* 反変[-T]

---

型パラメータでもサブタイプ関係をそのまま使える変異が共変[+T]になり、
多くのimmutableなコレクションが共変で作られています。

共変の話は後ほど

---

一方Arrayは非変で作られています。

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

scala> val arrayWater : Array[Water] = Array(water)

scala> val arrayDrink:Array[Drink] = arrayWater
<console>:16: error: type mismatch;

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
* サブタイプが使えると、要素が変更される
* 型安全が壊れてしまう
* 間違った型の要素を取り出す

---

これらの問題が出てくるので、Arrayは非変で作られています。

---

共変について

---


みなさんが使っているimmutableなListは共変です


```Scala
sealed abstract class List[+A] extends AbstractSeq[A]
```

---

共変ではサブタイプの関係をそのまま使えます。

```scala
scala> val list:List[Any] = List[Int](1,2,3)
list: List[Any] = List(1, 2, 3)

```

List[Int]をList[Any]に適合できるます。
#### (意訳すると)`List[Int]`を`List[Any]`と見なすことができます)


---

サブタイプの関係を使えるので、
ボトムタイプのNothing型を全ての型に適応できます。



<img src="https://github.com/taketora26/sample_gitpitch/blob/kansai/img/kaisou.png?raw=true">
---

反変について、なかなかメリットがわからないと思いますが、
実はみなさんがすでに使っているものの中に反変があります。

それがFunctionNです

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
* Intを引数にとり、Stringを返す関数です。

---

ここでわらしべ長者の話をします。

---

わらしべ長者は藁を拾い、

* アブを結んだ藁をみかんへ
* みかんを半物へ
* 半物を馬へ
* 馬を屋敷へ

交換していったお話です。
何かを渡して、何かを得る。
これ関数と同じ構造ですよね。

---

最初のシーンを関数を使うと次のようになります。

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