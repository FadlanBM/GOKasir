package Barang

import (
	"errors"
	"github.com/ProjectLSP/config"
	"github.com/ProjectLSP/internal/models"
	"github.com/ProjectLSP/internal/request"
	"github.com/ProjectLSP/internal/response"
	"github.com/gofiber/fiber/v2"
	"gorm.io/gorm"
	"strconv"
)

// Index godoc
// @Tags Crud Barang
// @Accept json
// @Produce json
// @Param nama query string false "Nama filter"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/barang [get]
func Index(c *fiber.Ctx) error {
	var barang []models.Barang

	// Get the value of the "category" parameter from the query string
	nama := c.Query("nama")

	// Create the base query
	query := config.DB

	// Add a WHERE condition based on the category parameter if provided
	if nama != "" {
		query = query.Where("name LIKE ?", "%"+nama+"%")
	}

	// Run the query
	if err := query.Find(&barang).Error; err != nil {
		return c.Status(500).JSON(fiber.Map{"Message": err.Error(), "Status": "Internal Server Error"})
	}

	var res []response.BarangResponse

	for _, v := range barang {
		barangResponse := response.BarangResponse{
			ID:         v.ID,
			Name:       v.Name,
			CodeBarang: v.CodeBarang,
			Merek:      v.Merek,
			Tipe:       v.Tipe,
			Price:      v.Price,
			Stock:      v.Stock,
		}
		res = append(res, barangResponse)
	}

	return c.Status(200).JSON(fiber.Map{"Message": "Success", "Data": res})
}

// Create godoc
// @Tags Crud Barang
// @Accept json
// @Produce json
// @Param request body request.BarangRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/barang [post]
func Create(c *fiber.Ctx) error {
	register := new(request.BarangRequest)

	err := c.BodyParser(register)

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	barang := models.Barang{
		Name:       register.Name,
		Merek:      register.Merek,
		CodeBarang: register.Code,
		Tipe:       register.Tipe,
		Price:      register.Price,
		Stock:      register.Stok,
	}

	if err := config.DB.Create(&barang).Error; err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": barang})
}

// DetailQR godoc
// @Tags Crud Barang
// @Accept json
// @Produce json
// @Param code path string true "Barang Code"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/barang/WithQr/{code} [get]
func DetailQR(c *fiber.Ctx) error {
	codeParam := c.Params("code")

	code, err := strconv.Atoi(codeParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var barang []models.Barang
	if err := config.DB.First(&barang, "code_barang = ?", code).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record not found"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var res []response.BarangResponse

	for _, v := range barang {
		voucerResponse := response.BarangResponse{
			ID:         v.ID,
			Name:       v.Name,
			CodeBarang: v.CodeBarang,
			Merek:      v.Merek,
			Tipe:       v.Tipe,
			Price:      v.Price,
			Stock:      v.Stock,
		}
		res = append(res, voucerResponse)
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": res})
}

// Detail godoc
// @Tags Crud Barang
// @Accept json
// @Produce json
// @Param id path int true "Barang ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/barang/{id} [get]
func Detail(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(400).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var barang models.Barang
	if err := config.DB.First(&barang, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Record not found"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Insert", "Message": "Successfully created", "Data": barang})
}

// Update godoc
// @Tags Crud Barang
// @Accept json
// @Produce json
// @Param id path int true "Member ID"
// @Param request body request.BarangRequest true "Request"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/barang/{id} [put]
func Update(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "Invalid ID format"})
	}

	var existingBarang models.Barang
	if err := config.DB.First(&existingBarang, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(fiber.StatusNotFound).JSON(fiber.Map{"Status": "Error", "Message": "ID not found"})
		}
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	var requestBarang request.BarangRequest
	if err := c.BodyParser(&requestBarang); err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	existingBarang.Name = requestBarang.Name
	existingBarang.CodeBarang = requestBarang.Code
	existingBarang.Merek = requestBarang.Merek
	existingBarang.Tipe = requestBarang.Tipe
	existingBarang.Price = requestBarang.Price
	existingBarang.Stock = requestBarang.Stok

	if err := config.DB.Save(&existingBarang).Error; err != nil {
		return c.Status(fiber.StatusInternalServerError).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	return c.Status(fiber.StatusOK).JSON(fiber.Map{"Status": "Success", "Message": "Member data updated"})
}

// Delete godoc
// @Tags Crud Barang
// @Accept json
// @Produce json
// @Param id path int true "Petugas ID"
// @Success 200 {object} response.ResponseDataSuccess
// @Failure 400 {object} response.ResponseError
// @Router /api/barang/{id} [delete]
func Delete(c *fiber.Ctx) error {
	idParam := c.Params("id")

	id, err := strconv.Atoi(idParam)
	if err != nil {
		return c.Status(fiber.StatusBadRequest).JSON(fiber.Map{"Status": "Error", "Message": "Invalid ID format"})
	}

	var barang models.Barang
	if err := config.DB.First(&barang, id).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Id Petugas tidak di temukan"})
		}
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	res := config.DB.Delete(&barang, id)

	if res.Error != nil {
		return c.Status(500).JSON(fiber.Map{"Status": "Error", "Message": err.Error()})
	}

	if res.RowsAffected == 0 {
		return c.Status(404).JSON(fiber.Map{"Status": "Error", "Message": "Petugas Not Found"})
	}
	return c.Status(200).JSON(fiber.Map{"Status": "Success", "Message": "Data Petugas Deleted"})
}
