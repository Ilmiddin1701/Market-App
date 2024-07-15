package uz.ilmiddin1701.asalariapp.models

class Product {
    var id: String? = null
    var name: String? = null
    var price: Long? = null
    var soni: Long? = null
    var date: String? = null
    var qrImgURL: String? = null


    constructor()

    constructor(id: String?, name: String?, price: Long?, soni: Long, date: String?, qrImgURL: String?) {
        this.id = id
        this.name = name
        this.price = price
        this.soni = soni
        this.date = date
        this.qrImgURL = qrImgURL
    }
}