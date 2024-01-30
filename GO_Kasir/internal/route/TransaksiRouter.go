package route

import (
	"github.com/ProjectLSP/internal/controller/Transaksi"
	"github.com/gofiber/fiber/v2"
)

func TransaksiRoute(c fiber.Router) {
	app := c.Group("/transaksi")
	/*	app.Use(middleware.APIKeyAuthMiddleware)
	 */app.Get("/:id", Transaksi.Index)
	app.Get("/detail/:id", Transaksi.IndexDetail)
	app.Get("/WithQr/:code", Transaksi.DetailQR)
	app.Post("/addmember", Transaksi.AddMember)
	app.Get("/voucher/:code", Transaksi.ValidasiVoucer)
	app.Post("/calculatePoint/:id", Transaksi.CalculatePoint)
	app.Post("/", Transaksi.Create)
	app.Post("/addBarangTransaksi", Transaksi.AddBarangTransaksi)
	app.Get("/barangTransaksi/:id", Transaksi.GetBarangTransaksi)
}
