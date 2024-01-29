package response

type TransaksiResponse struct {
	ID                uint    `json:"ID"`
	TotalPrice        float64 `json:"total_price"`
	NominalPembayaran float64 `json:"nominal_pembayaran"`
	Ppn               float64 `json:"ppn"`
	Kembalian         float64 `json:"kembalian"`
	NamaKaryawan      string  `json:"karyawan_name"`
	CodeVoucer        string  `json:"code_voucer"`
	MemberName        string  `json:"member_name"`
}
