package request

type PembelianBarangRequest struct {
	TransaksiID   uint    `json:"transaksi_id"`
	BarangID      uint    `json:"barang_id"`
	Quantity      uint    `json:"quantity"`
	SubTotalHarga float64 `json:"sub_total_harga"`
	Discount      float64 `json:"discount"`
}
