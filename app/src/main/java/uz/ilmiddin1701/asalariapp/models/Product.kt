package uz.ilmiddin1701.asalariapp.models

class Product {
    var name: String? = null
    var price: Int? = null
    var date: String? = null
    var qrImgURL: String? = null

    constructor()

    constructor(name: String?, price: Int?, date: String?, qrImgURL: String?) {
        this.name = name
        this.price = price
        this.date = date
        this.qrImgURL = qrImgURL
    }

    constructor(name: String?, price: Int?, date: String?) {
        this.name = name
        this.price = price
        this.date = date
    }


}