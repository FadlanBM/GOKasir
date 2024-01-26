package request

type BarangRequest struct {
	Name  string  `json:"name"`
	Merek string  `json:"merek"`
	Code  string  `json:"code"`
	Tipe  string  `json:"tipe"`
	Price float64 `json:"price"`
	Stok  int     `json:"stok"`
}
