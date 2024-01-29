package response

type BarangPembelianResponse struct {
	ID         uint    `json:"ID"`
	NamaBarang string  `json:"nama_barang"`
	CodeBarang string  `json:"code_barang"`
	Price      float64 `json:"price"`
	Quantity   string  `json:"quantity"`
}
