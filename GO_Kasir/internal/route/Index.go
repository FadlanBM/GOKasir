package route

import (
	"github.com/gofiber/fiber/v2"
)

func Index(r fiber.Router) {
	app := r.Group("/api")
	AuhtRoute(app)
	AdminRoute(app)
	KaryawanRoute(app)
	BarangRoute(app)
	VoucerRoute(app)
	BarangTranskasiRequest(app)
	MemberRoute(app)
	TokoProfileRoute(app)
	TransaksiRoute(app)
	MeRoute(app)
	RegisterKaryawan(app)
}
